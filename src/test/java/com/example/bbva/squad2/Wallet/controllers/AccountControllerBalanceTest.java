package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.AccountBalanceDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.models.AccountStatic;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AccountControllerBalanceTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @Mock
    private UsuarioLoggeadoService usuarioLoggeadoService;

    @Mock
    private HttpServletRequest request;

    private UsuarioSeguridad mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new UsuarioSeguridad();
        mockUser.setId(1L); // Simular un usuario con ID 1
    }

    @Test
    void testGetBalance_Success() {
        // Arrange
        AccountStatic.AccountBalance mockAccountArs = new AccountStatic.AccountBalance(1500.0, "ARS");
        AccountStatic.AccountBalance mockAccountUsd = new AccountStatic.AccountBalance(700.0, "USD");

        AccountBalanceDTO mockBalance = AccountBalanceDTO.builder()
                .accountArs(mockAccountArs)
                .accountUsd(mockAccountUsd)
                .build();

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUser);
        when(accountService.getBalanceByUserId(mockUser.getId())).thenReturn(mockBalance);

        // Act
        ResponseEntity<AccountBalanceDTO> response = accountController.getBalance(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1500.0, response.getBody().getAccountArs().getBalance());
        assertEquals(700.0, response.getBody().getAccountUsd().getBalance());
    }

    @Test
    void testGetBalance_UserNotFound() {
        // Arrange
        when(usuarioLoggeadoService.getInfoUserSecurity(request))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountController.getBalance(request);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testGetBalance_NoAccounts() {
        // Arrange
        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUser);
        when(accountService.getBalanceByUserId(mockUser.getId()))
                .thenThrow(new RuntimeException("No se encontraron cuentas asociadas al usuario"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountController.getBalance(request);
        });

        assertEquals("No se encontraron cuentas asociadas al usuario", exception.getMessage());
    }

    @Test
    void testGetBalance_NullBalance() {
        // Arrange
        AccountBalanceDTO mockBalance = AccountBalanceDTO.builder()
                .accountArs(null) // Simular un balance nulo en ARS
                .accountUsd(null) // Simular un balance nulo en USD
                .build();

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUser);
        when(accountService.getBalanceByUserId(mockUser.getId())).thenReturn(mockBalance);

        // Act
        ResponseEntity<AccountBalanceDTO> response = accountController.getBalance(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getAccountArs());
        assertNull(response.getBody().getAccountUsd());
    }
}
