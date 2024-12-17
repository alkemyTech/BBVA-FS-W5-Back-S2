package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.dtos.EditLimitDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountControllerTest {

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
        mockUser = new UsuarioSeguridad(1L, "testUser", "USER", null, null);
    }

    @Test
    void testUpdateTransactionLimit_Success() {
        // Arrange
        Long accountId = 1L;
        Double newTransactionLimit = 5000.0;
        AccountDTO mockResponse = new AccountDTO(accountId, "cbu123", CurrencyTypeEnum.ARS, newTransactionLimit, 10000.0);
        EditLimitDTO editLimitDTO = new EditLimitDTO(accountId, newTransactionLimit);
        when(usuarioLoggeadoService.getInfoUserSecurity(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(accountService.updateTransactionLimit(accountId, mockUser.getId(), newTransactionLimit)).thenReturn(mockResponse);

        // Act
        ResponseEntity<AccountDTO> response = accountController.updateTransactionLimit(editLimitDTO, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testUpdateTransactionLimit_AccountNotFound() {
        // Arrange
        Long accountId = 999L;
        Double newTransactionLimit = 5000.0;
        EditLimitDTO editLimitDTO = new EditLimitDTO(accountId, newTransactionLimit);
        when(usuarioLoggeadoService.getInfoUserSecurity(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(accountService.updateTransactionLimit(accountId, mockUser.getId(), newTransactionLimit))
                .thenThrow(new WalletsException(HttpStatus.NOT_FOUND, "Cuenta no encontrada."));

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () ->
                accountController.updateTransactionLimit(editLimitDTO, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cuenta no encontrada.", exception.getMessage());
    }

    @Test
    void testUpdateTransactionLimit_InvalidTransactionLimit() {
        // Arrange
        Long accountId = 1L;
        Double newTransactionLimit = -5000.0;
        EditLimitDTO editLimitDTO = new EditLimitDTO(accountId, newTransactionLimit);
        when(usuarioLoggeadoService.getInfoUserSecurity(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(accountService.updateTransactionLimit(accountId, mockUser.getId(), newTransactionLimit))
                .thenThrow(new WalletsException(HttpStatus.BAD_REQUEST, "El limite de transacción no puede ser nulo."));

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () ->
                accountController.updateTransactionLimit(editLimitDTO, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("El limite de transacción no puede ser nulo.", exception.getMessage());
    }

    @Test
    void testUpdateTransactionLimit_Forbidden() {
        // Arrange
        Long accountId = 1L;
        Double newTransactionLimit = 5000.0;
        EditLimitDTO editLimitDTO = new EditLimitDTO(accountId, newTransactionLimit);
        when(usuarioLoggeadoService.getInfoUserSecurity(any(HttpServletRequest.class))).thenReturn(mockUser);
        when(accountService.updateTransactionLimit(accountId, mockUser.getId(), newTransactionLimit))
                .thenThrow(new WalletsException(HttpStatus.FORBIDDEN, "No esta autorizado para modificar esta cuenta."));

        // Act & Assert
        WalletsException exception = assertThrows(WalletsException.class, () ->
                accountController.updateTransactionLimit(editLimitDTO, request));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("No esta autorizado para modificar esta cuenta.", exception.getMessage());
    }
}
