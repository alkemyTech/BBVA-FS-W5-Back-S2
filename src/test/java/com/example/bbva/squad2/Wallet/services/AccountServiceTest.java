package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.dtos.AccountBalanceDTO;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.repositories.AccountsRepository;
import com.example.bbva.squad2.Wallet.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FixedTermDepositService fixedTermDepositService;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAccountsByUser_Success() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        Account account1 = new Account();
        account1.setId(1L);
        Account account2 = new Account();
        account2.setId(2L);

        List<Account> accounts = List.of(account1, account2);
        mockUser.setAccounts(accounts);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        List<AccountDTO> result = accountService.getAccountsByUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetAccountsByUser_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        List<AccountDTO> result = accountService.getAccountsByUser(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateAccount_Success() {
        // Arrange
        Long userId = 1L;
        CurrencyTypeEnum currency = CurrencyTypeEnum.USD;

        User mockUser = new User();
        mockUser.setId(userId);

        Account mockAccount = new Account();
        mockAccount.setId(1L);
        mockAccount.setCurrency(currency);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(accountsRepository.save(any(Account.class))).thenReturn(mockAccount);

        // Act
        AccountDTO result = accountService.createAccount(userId, currency);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(userId);
        verify(accountsRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_UserNotFound() {
        // Arrange
        Long userId = 1L;
        CurrencyTypeEnum currency = CurrencyTypeEnum.USD;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class,
                () -> accountService.createAccount(userId, currency));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetBalanceByUserId_Success() {
        // Arrange
        Long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);

        Account accountArs = new Account();
        accountArs.setCurrency(CurrencyTypeEnum.ARS);
        accountArs.setBalance(1000.0);

        Account accountUsd = new Account();
        accountUsd.setCurrency(CurrencyTypeEnum.USD);
        accountUsd.setBalance(500.0);

        mockUser.setAccounts(List.of(accountArs, accountUsd));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        AccountBalanceDTO result = accountService.getBalanceByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1000.0, result.getAccountArs().getBalance());
        assertEquals(500.0, result.getAccountUsd().getBalance());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetBalanceByUserId_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getBalanceByUserId(userId));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}
