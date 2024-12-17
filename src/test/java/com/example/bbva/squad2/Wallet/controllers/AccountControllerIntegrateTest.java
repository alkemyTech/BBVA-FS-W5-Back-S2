package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.*;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.AccountStatic;
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

import java.util.List;

import static com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AccountControllerIntegrateTest {

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
        mockUser.setId(1L);
    }

    @Test
    void testGetAccounts_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        AccountDTO mockAccount = new AccountDTO();
        mockAccount.setId(1L);
        mockAccount.setCurrency(USD);
        when(accountService.getAccountsByUser(userId)).thenReturn(List.of(mockAccount));

        // Act
        ResponseEntity<List<AccountDTO>> response = accountController.getAccountsUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockAccount, response.getBody().get(0));
    }

    @Test
    void testGetAccounts_SuccessLogged() throws Exception {
        // Arrange
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        UsuarioSeguridad mockUsuarioSeguridad = new UsuarioSeguridad();
        mockUsuarioSeguridad.setId(1L);
        AccountDTO mockAccount = new AccountDTO();
        mockAccount.setId(1L);
        mockAccount.setCurrency(CurrencyTypeEnum.USD);

        when(usuarioLoggeadoService.getInfoUserSecurity(mockRequest)).thenReturn(mockUsuarioSeguridad);
        when(accountService.getAccountsByUser(1L)).thenReturn(List.of(mockAccount));

        // Act
        ResponseEntity<List<AccountDTO>> response = accountController.getAccounts(mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockAccount, response.getBody().get(0));
    }


    @Test
    void testCreateAccount_Success() {
        // Arrange
        CurrencyTypeEnum currency = USD;
        AccountDTO mockAccount = new AccountDTO();
        mockAccount.setId(1L);
        mockAccount.setCurrency(currency);
        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUser);
        when(accountService.createAccount(anyLong(), eq(currency))).thenReturn(mockAccount);

        // Act
        ResponseEntity<AccountDTO> response = accountController.createAccount(request, currency);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAccount, response.getBody());
    }

    @Test
    void testGetBalance_Success() {
        // Arrange
        AccountStatic.AccountBalance mockAccountArs = new AccountStatic.AccountBalance(1000.0, "ARS");
        mockAccountArs.setBalance(1000.0);  // Establecer el balance en ARS

        AccountStatic.AccountBalance mockAccountUsd = new AccountStatic.AccountBalance(500.0, "USD");
        mockAccountUsd.setBalance(500.0);  // Establecer el balance en USD

        AccountBalanceDTO mockBalance = new AccountBalanceDTO();
        mockBalance.setAccountArs(mockAccountArs);  // Asignar balance ARS
        mockBalance.setAccountUsd(mockAccountUsd);  // Asignar balance USD

        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUser);
        when(accountService.getBalanceByUserId(mockUser.getId())).thenReturn(mockBalance);

        // Act
        ResponseEntity<AccountBalanceDTO> response = accountController.getBalance(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verificar el balance en ARS
        assertEquals(1000.0, response.getBody().getAccountArs().getBalance());

        // Verificar el balance en USD
        assertEquals(500.0, response.getBody().getAccountUsd().getBalance());
    }


    @Test
    void testUpdateTransactionLimit_Success() {
        // Arrange
        Long accountId = 1L;
        Double newTransactionLimit = 5000.0;
        AccountDTO mockUpdatedAccount = new AccountDTO();
        EditLimitDTO editLimitDTO = new EditLimitDTO(accountId, newTransactionLimit);
        mockUpdatedAccount.setId(accountId);
        when(usuarioLoggeadoService.getInfoUserSecurity(request)).thenReturn(mockUser);
        when(accountService.updateTransactionLimit(eq(accountId), eq(mockUser.getId()), eq(newTransactionLimit)))
                .thenReturn(mockUpdatedAccount);

        // Act
        ResponseEntity<AccountDTO> response = accountController.updateTransactionLimit(editLimitDTO, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockUpdatedAccount, response.getBody());
    }

    @Test
    void testGetAllAccounts_Success() {
        // Arrange
        PageableResponseDTO<AccountDTO> mockPageableResponse = new PageableResponseDTO<>();
        mockPageableResponse.setData(List.of(new AccountDTO()));
        mockPageableResponse.setTotalPages(1);
        when(accountService.getAllAccountsPaginated(0, 10)).thenReturn(mockPageableResponse);

        // Act
        ResponseEntity<?> response = accountController.getAllAccounts(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetAllAccounts_Error_InvalidPage() {
        // Act
        ResponseEntity<?> response = accountController.getAllAccounts(-1, 10);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Los valores de página y tamaño deben ser positivos.", response.getBody());
    }

    @Test
    void testGetAllAccounts_Error_InvalidSize() {
        // Act
        ResponseEntity<?> response = accountController.getAllAccounts(0, -1);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Los valores de página y tamaño deben ser positivos.", response.getBody());
    }
}
