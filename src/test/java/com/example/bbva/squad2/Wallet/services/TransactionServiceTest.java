package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.SendTransactionDTO;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.TransactionsRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendTransaction_ShouldThrowException_WhenSenderAccountNotFound() {
        // Arrange
        SendTransactionDTO dto = new SendTransactionDTO();
        dto.setCurrency(CurrencyTypeEnum.USD);
        dto.setDestinationCbu("1234567890");
        dto.setAmount(100.0);

        String username = "user@example.com";

        when(accountsRepository.findByCurrencyAndUser_Email(dto.getCurrency(), username))
                .thenReturn(Optional.empty());

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () -> {
            transactionService.sendTransaction(dto, username);
        });

        assertEquals("Cuenta emisora no encontrada para el usuario autenticado con la moneda especificada.", exception.getMessage());
    }

    @Test
    void sendTransaction_ShouldThrowException_WhenDestinationAccountNotFound() {
        // Arrange
        SendTransactionDTO dto = new SendTransactionDTO();
        dto.setCurrency(CurrencyTypeEnum.USD);
        dto.setDestinationCbu("1234567890");
        dto.setAmount(100.0);

        String username = "user@example.com";

        Account senderAccount = new Account();
        senderAccount.setUser(new User());
        senderAccount.setBalance(500.0);

        when(accountsRepository.findByCurrencyAndUser_Email(dto.getCurrency(), username))
                .thenReturn(Optional.of(senderAccount));
        when(accountsRepository.findByCbuAndCurrency(dto.getDestinationCbu(), dto.getCurrency()))
                .thenReturn(Optional.empty());

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () -> {
            transactionService.sendTransaction(dto, username);
        });

        assertEquals("Cuenta destinataria no encontrada con el CBU especificado.", exception.getMessage());
    }

    @Test
    void sendTransaction_ShouldThrowException_WhenInsufficientBalance() {
        // Arrange
        SendTransactionDTO dto = new SendTransactionDTO();
        dto.setCurrency(CurrencyTypeEnum.USD);
        dto.setDestinationCbu("1234567890");
        dto.setAmount(600.0);

        String username = "user@example.com";

        // Crear y configurar el usuario emisor
        User senderUser = new User();
        senderUser.setId(1L);

        // Crear y configurar la cuenta emisora
        Account senderAccount = new Account();
        senderAccount.setUser(senderUser);
        senderAccount.setBalance(500.0);

        // Crear y configurar el usuario receptor
        User destinationUser = new User();
        destinationUser.setId(2L);

        // Crear y configurar la cuenta receptora
        Account destinationAccount = new Account();
        destinationAccount.setUser(destinationUser);

        // Configurar los mocks
        when(accountsRepository.findByCurrencyAndUser_Email(dto.getCurrency(), username))
                .thenReturn(Optional.of(senderAccount));
        when(accountsRepository.findByCbuAndCurrency(dto.getDestinationCbu(), dto.getCurrency()))
                .thenReturn(Optional.of(destinationAccount));

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () -> {
            transactionService.sendTransaction(dto, username);
        });

        assertEquals("Saldo insuficiente en la cuenta emisora.", exception.getMessage());
    }


    @Test
    void sendTransaction_ShouldCompleteTransaction_WhenValid() {
        // Arrange
        SendTransactionDTO dto = new SendTransactionDTO();
        dto.setCurrency(CurrencyTypeEnum.USD);
        dto.setDestinationCbu("1234567890");
        dto.setAmount(100.0);

        String username = "user@example.com";

        // Crear y configurar el usuario emisor
        User senderUser = new User();
        senderUser.setId(1L);

        // Crear y configurar la cuenta emisora
        Account senderAccount = new Account();
        senderAccount.setUser(senderUser);
        senderAccount.setBalance(500.0);
        senderAccount.setTransactionLimit(200.0);

        // Crear y configurar el usuario receptor
        User destinationUser = new User();
        destinationUser.setId(2L);

        // Crear y configurar la cuenta receptora
        Account destinationAccount = new Account();
        destinationAccount.setUser(destinationUser);
        destinationAccount.setBalance(300.0);

        // Configurar los mocks
        when(accountsRepository.findByCurrencyAndUser_Email(dto.getCurrency(), username))
                .thenReturn(Optional.of(senderAccount));
        when(accountsRepository.findByCbuAndCurrency(dto.getDestinationCbu(), dto.getCurrency()))
                .thenReturn(Optional.of(destinationAccount));

        // Act
        assertDoesNotThrow(() -> transactionService.sendTransaction(dto, username));

        // Assert
        assertEquals(400.0, senderAccount.getBalance());
        assertEquals(400.0, destinationAccount.getBalance());
        verify(accountsRepository, times(1)).save(senderAccount);
        verify(accountsRepository, times(1)).save(destinationAccount);
    }

}
