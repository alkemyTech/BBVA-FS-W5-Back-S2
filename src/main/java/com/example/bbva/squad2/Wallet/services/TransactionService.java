package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.AlkemyException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.TransactionsRepository;
import com.example.bbva.squad2.Wallet.config.JwtServices;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TransactionService {

    private final TransactionsRepository transactionRepository;
    private final AccountsRepository accountsRepository;
    private final JwtServices jwtServices;

    @Autowired
    public TransactionService(
            TransactionsRepository transactionRepository,
            AccountsRepository accountsRepository,
            JwtServices jwtServices
    ) {
        this.transactionRepository = transactionRepository;
        this.accountsRepository = accountsRepository;
        this.jwtServices = jwtServices;
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByAccount_User_Id(userId);
    }

    public void sendTransaction(SendTransactionDTO dto, HttpServletRequest request) throws AlkemyException {
        String token = request.getHeader("Authorization");
        token = token.substring(7);

        UsuarioSeguridad usuarioSeguridad = jwtServices.validateAndGetSecurity(token);

        // Buscar la cuenta emisora a través del email del usuario autenticado (extraído del token)
        Account senderAccount = accountsRepository.findByCurrencyAndUser_Email(dto.getCurrency(), usuarioSeguridad.getUsername())
                .orElseThrow(() -> new AlkemyException(
                    HttpStatus.NOT_FOUND,
                    "Cuenta emisora no encontrada para el usuario autenticado con la moneda especificada."
                ));

        // Buscar la cuenta destinataria usando el CBU del DTO
        Account destinationAccount = accountsRepository.findByCbuAndCurrency(dto.getDestinationCbu(), dto.getCurrency())
                .orElseThrow(() -> new AlkemyException(
                        HttpStatus.NOT_FOUND,
                        "Cuenta destinataria no encontrada con el CBU especificado."
                ));

        // Validar que la cuenta emisora y destinataria no pertenezcan al mismo usuario
        if (senderAccount.getUser().getId().equals(destinationAccount.getUser().getId())) {
            throw new AlkemyException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede realizar una transferencia a una cuenta propia."
            );
        }

        // Validar si el monto a transferir es menor o igual al balance de la cuenta emisora
        if (dto.getAmount() > senderAccount.getBalance()) {
            throw new AlkemyException(
                    HttpStatus.BAD_REQUEST,
                    "Saldo insuficiente en la cuenta emisora."
            );
        }

        // Validar si el monto a transferir está dentro del límite permitido por la cuenta emisora
        if (dto.getAmount() > senderAccount.getTransactionLimit()) {
            throw new AlkemyException(
                    HttpStatus.BAD_REQUEST,
                    "El monto excede el límite de transacciones de la cuenta emisora."
            );
        }

        // Crear y registrar la transacción para el usuario emisor (PAYMENT)
        createTransaction(senderAccount, senderAccount.getCbu(), destinationAccount.getCbu(), dto.getAmount(), TransactionTypeEnum.PAGO, dto.getDescription());

        // Crear y registrar la transacción para el usuario receptor (INCOME)
        createTransaction(destinationAccount, senderAccount.getCbu(), destinationAccount.getCbu(), dto.getAmount(), TransactionTypeEnum.INGRESO, dto.getDescription());

        // Actualizar balances en ambas cuentas
        senderAccount.setBalance(senderAccount.getBalance() - dto.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + dto.getAmount());

        // Guardar las cuentas actualizadas
        accountsRepository.save(senderAccount);
        accountsRepository.save(destinationAccount);
    }

    private void createTransaction(Account account, String cbuOrigen, String cbuDestino, Double amount, TransactionTypeEnum type, String description) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .CbuDestino(cbuDestino)
                .CbuOrigen(cbuOrigen)
                .type(type)
                .description(description)
                .build();

        transactionRepository.save(transaction);
    }

    public DepositDTO deposit(SendDepositDTO dto, HttpServletRequest request, String accountCBU) throws AlkemyException {
        // Extraer y validar el token del usuario
        String token = request.getHeader("Authorization").substring(7);
        UsuarioSeguridad usuarioSeguridad = jwtServices.validateAndGetSecurity(token);

        Account account = accountsRepository.findByCBU(accountCBU)
                .orElseThrow(() -> new AlkemyException(
                        HttpStatus.NOT_FOUND,
                        "Cuenta no encontrada."
                ));

        if (!account.getUser().getEmail().equals(usuarioSeguridad.getUsername())) {
            throw new AlkemyException(
                    HttpStatus.UNAUTHORIZED,
                    "No tienes permiso para realizar esta operación en una cuenta que no te pertenece."
            );
        }

        // Validar el monto del depósito
        if (dto.getAmount() <= 0) {
            throw new AlkemyException(
                    HttpStatus.BAD_REQUEST,
                    "El monto a depositar debe ser mayor a cero."
            );
        }

        // Crear la transacción de depósito
        Transaction transaction = Transaction.builder()
                .CbuOrigen("External") // Depósito no tiene origen
                .CbuDestino(account.getCbu())
                .amount(dto.getAmount())
                .type(TransactionTypeEnum.DEPOSITO)
                .description(dto.getDescription())
                .account(account)
                .build();

        transactionRepository.save(transaction);

        // Actualizar el balance de la cuenta
        account.setBalance(account.getBalance() + dto.getAmount());
        accountsRepository.save(account);

        // Mapear los datos para la respuesta
        TransactionBalanceDTO transactionDTO = new TransactionBalanceDTO().mapFromTransaction(transaction);
        AccountDTO accountDTO = new AccountDTO().mapFromAccount(account);

        // Crear y devolver el DTO de respuesta
        return DepositDTO.builder()
                .transaction(transactionDTO)
                .account(accountDTO)
                .build();
    }


}

