package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.FixedTermSimulationDTO;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.FixedTermDepositRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FixedTermDepositServiceTest {

    @InjectMocks
    private FixedTermDepositService fixedTermDepositService;

    @Mock
    private FixedTermDepositRepository fixedTermDepositRepository;

    @Mock
    private AccountsRepository accountsRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFixedTermDeposit_ShouldThrowException_WhenDaysLessThan30() {
        // Arrange
        Long userId = 1L;
        Double amount = 1000.0;
        Integer days = 20;

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () -> {
            fixedTermDepositService.createFixedTermDeposit(userId, amount, days, false);
        });

        assertEquals("El plazo fijo debe ser de al menos 30 días.", exception.getMessage());
    }

    @Test
    void createFixedTermDeposit_ShouldThrowException_WhenUserHasNoPesoAccount() {
        // Arrange
        Long userId = 1L;
        Double amount = 1000.0;
        Integer days = 30;

        // Mockear el comportamiento para simular que no existe una cuenta en pesos
        when(accountsRepository.findByUserIdAndCurrency(userId, CurrencyTypeEnum.ARS))
                .thenReturn(Optional.empty());

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () -> {
            fixedTermDepositService.createFixedTermDeposit(userId, amount, days, false);
        });

        // Verificar el mensaje de error
        assertEquals("El usuario no tiene una cuenta en pesos.", exception.getMessage());
    }

    @Test
    void createFixedTermDeposit_ShouldThrowException_WhenInsufficientBalance() {
        // Arrange
        Long userId = 1L;
        Double amount = 1000.0;
        Integer days = 30;

        Account account = new Account();
        account.setBalance(500.0);

        when(accountsRepository.findByUserIdAndCurrency(userId, CurrencyTypeEnum.ARS))
                .thenReturn(Optional.of(account));

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () -> {
            fixedTermDepositService.createFixedTermDeposit(userId, amount, days, false);
        });

        assertEquals("Saldo insuficiente para crear el plazo fijo.", exception.getMessage());
    }

    @Test
    void processMaturedFixedTermDeposits_ShouldProcessDeposits_WhenDepositsAreMatured() {
        // Arrange
        Account account = new Account();
        account.setBalance(2000.0);

        FixedTermDeposit deposit = FixedTermDeposit.builder()
                .amount(1000.0)
                .interest(300.0)
                .account(account)
                .creationDate(LocalDateTime.now().minusDays(30))
                .closingDate(LocalDateTime.now().minusMinutes(1))
                .processed(false)
                .build();

        when(fixedTermDepositRepository.findByClosingDateBeforeAndProcessedFalse(LocalDateTime.now()))
                .thenReturn(List.of(deposit));

        // Act
        fixedTermDepositService.processMaturedFixedTermDeposits();

        // Assert
        assertEquals(2000.0, account.getBalance());
        assertTrue(!deposit.getProcessed());
    }
}
