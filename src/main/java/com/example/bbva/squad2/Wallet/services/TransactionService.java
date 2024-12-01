package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.TransactionsRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionsRepository transactionRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private UserRepository ur;

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByAccount_User_Id(userId);
    }

    public void sendTransaction(SendTransactionDTO dto, String usernamen) throws WalletsException {

        // Buscar la cuenta emisora a través del email del usuario autenticado (extraído del token)
        Account senderAccount = accountsRepository.findByCurrencyAndUser_Email(dto.getCurrency(), usernamen)
                .orElseThrow(() -> new WalletsException(
                        HttpStatus.NOT_FOUND,
                        "Cuenta emisora no encontrada para el usuario autenticado con la moneda especificada."
                ));

        // Buscar la cuenta destinataria usando el CBU del DTO
        Account destinationAccount = accountsRepository.findByCbuAndCurrency(dto.getDestinationCbu(), dto.getCurrency())
                .orElseThrow(() -> new WalletsException(
                        HttpStatus.NOT_FOUND,
                        "Cuenta destinataria no encontrada con el CBU especificado."
                ));

        // Validar que la cuenta emisora y destinataria no pertenezcan al mismo usuario
        if (senderAccount.getUser().getId().equals(destinationAccount.getUser().getId())) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede realizar una transferencia a una cuenta propia."
            );
        }

        // Validar si el monto a transferir es menor o igual al balance de la cuenta emisora
        if (dto.getAmount() > senderAccount.getBalance()) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "Saldo insuficiente en la cuenta emisora."
            );
        }

        // Validar si el monto a transferir está dentro del límite permitido por la cuenta emisora
        if (dto.getAmount() > senderAccount.getTransactionLimit()) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "El monto excede el límite de transacciones de la cuenta emisora."
            );
        }

        // Crear y registrar la transacción para el usuario emisor (PAYMENT)
        createTransaction(senderAccount,
                senderAccount.getCbu(),
                destinationAccount.getCbu(),
                dto.getAmount(),
                TransactionTypeEnum.PAGO,
                dto.getDescription()
        );

        // Crear y registrar la transacción para el usuario receptor (INCOME)
        createTransaction(destinationAccount,
                senderAccount.getCbu(),
                destinationAccount.getCbu(),
                dto.getAmount(),
                TransactionTypeEnum.INGRESO,
                dto.getDescription()
        );

        // Actualizar balances en ambas cuentas
        senderAccount.setBalance(senderAccount.getBalance() - dto.getAmount());
        destinationAccount.setBalance(destinationAccount.getBalance() + dto.getAmount());

        // Guardar las cuentas actualizadas
        accountsRepository.save(senderAccount);
        accountsRepository.save(destinationAccount);
    }

    public DepositDTO deposit(SendDepositDTO dto, String accountCBU, String username) throws WalletsException {

        Account account = accountsRepository.findBycbu(accountCBU)
                .orElseThrow(() -> new WalletsException(
                        HttpStatus.NOT_FOUND,
                        "Cuenta no encontrada."
                ));

        if (!account.getUser().getEmail().equals(username)) {
            throw new WalletsException(
                    HttpStatus.UNAUTHORIZED,
                    "No tienes permiso para realizar esta operación en una cuenta que no te pertenece."
            );
        }

        // Validar el monto del depósito
        if (dto.getAmount() <= 0) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "El monto a depositar debe ser mayor a cero."
            );
        }

        // Crear la transacción de depósito
        Transaction transaction  = createTransaction(account,
                "External",
                account.getCbu(),
                dto.getAmount(),
                TransactionTypeEnum.DEPOSITO,
                dto.getDescription()
        );

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

    public DepositDTO payment(SendPaymentDTO dto, Long idUser) throws WalletsException {
        // Validar monto mayor a cero
        if (dto.getAmount() <= 0) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "El monto a depositar debe ser mayor a cero."
            );
        }

        Optional<User> userOpt = ur.findById(idUser);
        if (userOpt.isEmpty()) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "Usuario no autorizado."
            );
        }

        User user = userOpt.get();

        // Validar que el usuario tenga una cuenta con la moneda indicada
        Optional<Account> cuentaOpt = user.getAccounts().stream()
                .filter(cuenta -> cuenta.getCurrency().equals(dto.getCurrency()))
                .findFirst();

        if (cuentaOpt.isEmpty()) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "No se encontró una cuenta con la moneda especificada."
            );
        }

        Account cuenta = cuentaOpt.get();

        // Validar saldo suficiente en la cuenta
        if (cuenta.getBalance() < dto.getAmount()) {
            throw new WalletsException(
                    HttpStatus.BAD_REQUEST,
                    "Saldo insuficiente en la cuenta."
            );
        }

        // Crear y registrar la transacción de pago
        Transaction transaccion = createTransaction(
                cuenta,
                cuenta.getCbu(),
                "Pago de tarjeta",
                dto.getAmount(),
                TransactionTypeEnum.PAGO,
                dto.getDescription()
        );

        // Actualizar el balance de la cuenta
        cuenta.setBalance(cuenta.getBalance() - dto.getAmount());
        accountsRepository.save(cuenta);

        TransactionBalanceDTO transactionDTO = new TransactionBalanceDTO().mapFromTransaction(transaccion);
        AccountDTO accountDTO = new AccountDTO().mapFromAccount(cuenta);

        // Construir la respuesta con la transacción y la cuenta afectada
        return DepositDTO.builder()
                .transaction(transactionDTO)
                .account(accountDTO)
                .build();
    }

    public Transaction createTransaction(Account account, String cbuOrigen, String cbuDestino, Double amount, TransactionTypeEnum type, String description) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .CbuDestino(cbuDestino)
                .CbuOrigen(cbuOrigen)
                .type(type)
                .description(description)
                .build();

        transactionRepository.save(transaction);

        return transaction;
    }

    public TransactionListDTO getTransactionById(Long transactionId, Long userId) {
        return transactionRepository.findByAccount_User_Id(userId).stream()
                .filter(transaction -> transaction.getId().equals(transactionId))
                .findFirst()
                .map(transaction -> new TransactionListDTO().fromEntity(transaction)) // Convertir a DTO si existe
                .orElseThrow(() -> new WalletsException(HttpStatus.UNAUTHORIZED, "No existe la transacción solicitada para esa cuenta"));
    }

    public PageableResponseDTO<TransactionListDTO> getTransactionsByUserIdPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Obtener la página de transacciones
        Page<Transaction> transactionPage = transactionRepository.findByAccount_User_Id(userId, pageable);

        // Mapear las entidades a DTOs
        List<TransactionListDTO> transactionDTOs = transactionPage.getContent().stream()
                .map(TransactionListDTO::fromEntity)
                .toList();

        // Construir y devolver la respuesta paginada
        return new PageableResponseDTO<>(
                transactionDTOs,
                transactionPage.getNumber(),
                transactionPage.getTotalPages(),
                transactionPage.hasPrevious() ? "/transactions/user/" + userId + "?page=" + (page - 1) + "&size=" + size : null,
                transactionPage.hasNext() ? "/transactions/user/" + userId + "?page=" + (page + 1) + "&size=" + size : null
        );
    }

    public List<TransactionListDTO> getTransactionDtosByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByAccount_User_Id(userId);

        // Mapear las entidades a DTOs
        return transactions.stream()
                .map(transaction -> TransactionListDTO.fromEntity(transaction))
                .toList();
    }
}
