package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.dtos.FixedTermDTO;
import com.example.bbva.squad2.Wallet.dtos.FixedTermSimulationDTO;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.FixedTermDepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FixedTermDepositService {

    private final FixedTermDepositRepository fixedTermDepositRepository;

    @Autowired
    private AccountsRepository accountRepository;

    @Value("${fixed-term.daily-interest-rate}")
    private double dailyInterestRate; // Tomo la tasa de interés desde el archivo de configuración


    public FixedTermDepositService(FixedTermDepositRepository fixedTermDepositRepository) {
        this.fixedTermDepositRepository = fixedTermDepositRepository;
    }

    public List<FixedTermDTO> getFixedTermDepositsByUserId(Long userId) {
        // Obtener directamente los plazos fijos del usuario logueado

        return fixedTermDepositRepository.findByAccountUserId(userId)
                .stream()
                .map(fixedTerm -> new FixedTermDTO().mapFromFixedTerm(fixedTerm))
                .collect(Collectors.toList());

    }

    public List<FixedTermDeposit> getAllFixedTermDeposits() {
        return fixedTermDepositRepository.findAll(); // Devuelve todos los registros
    }

    public FixedTermSimulationDTO createFixedTermDeposit(Long userId, Double amount, Integer days, String cbu, boolean simulation) throws WalletsException{
        if (days < 30) {
            throw new WalletsException(HttpStatus.BAD_REQUEST, "El plazo fijo debe ser de al menos 30 días.");
        }

        // Obtener la cuenta en pesos del usuario
        Account account = accountRepository.findByCbu(cbu)
                .orElseThrow(() -> new WalletsException(HttpStatus.NOT_FOUND, "El usuario no tiene una cuenta en pesos."));

        // Validar que el balance es suficiente
        if (account.getBalance() < amount) {
            throw new WalletsException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para crear el plazo fijo.");
        }

        // Calcular el interés total
        double totalInterest = amount * dailyInterestRate * days;

        // Restar el monto del balance de la cuenta
        if(!simulation) {
            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);

            // Crear el plazo fijo
            FixedTermDeposit fixedTermDeposit = FixedTermDeposit.builder()
                    .amount(amount)
                    .account(account)
                    .interest(totalInterest)
                    .creationDate(LocalDateTime.now())
                    //.closingDate(LocalDateTime.now().plusDays(days)) descomentar luego
                    .closingDate(LocalDateTime.now().plusMinutes(1))
                    .processed(false)
                    .build();

            FixedTermDeposit savedDeposit = fixedTermDepositRepository.save(fixedTermDeposit);

            return new FixedTermSimulationDTO().mapFromFixedTerm(savedDeposit);
        } else {
            FixedTermDeposit fixedTermDepositSimulation = FixedTermDeposit.builder()
                    .amount(amount)
                    .account(account)
                    .interest(totalInterest)
                    .creationDate(LocalDateTime.now())
                    //.closingDate(LocalDateTime.now().plusDays(days)) descomentar luego
                    .closingDate(LocalDateTime.now().plusMinutes(1))
                    .processed(false)
                    .build();

            return new FixedTermSimulationDTO().mapFromFixedTerm(fixedTermDepositSimulation);
        }
    }

    @Scheduled(fixedRate = 60000) // Se ejecuta cada minuto, a modo de prueba
    public void processMaturedFixedTermDeposits() {
        // Obtener todos los plazos fijos vencidos y no procesados
        List<FixedTermDeposit> maturedDeposits = fixedTermDepositRepository.findByClosingDateBeforeAndProcessedFalse(LocalDateTime.now());

        for (FixedTermDeposit deposit : maturedDeposits) {
            Account account = deposit.getAccount();
            double totalAmount = deposit.getAmount() + deposit.getInterest();

            // Sumar el monto e interés al balance de la cuenta
            account.setBalance(account.getBalance() + totalAmount);
            accountRepository.save(account);

            // Marcar el plazo fijo como procesado
            deposit.setProcessed(true);
            fixedTermDepositRepository.save(deposit);
        }
    }
}
