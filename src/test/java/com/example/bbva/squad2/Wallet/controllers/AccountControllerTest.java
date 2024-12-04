package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import com.example.bbva.squad2.Wallet.exceptions.WalletsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private UsuarioLoggeadoService usuarioLoggeadoService;

    @InjectMocks
    private AccountController accountController;

    private UsuarioSeguridad usuarioSeguridad;

    @BeforeEach
    public void setUp() {
        // Configuración inicial de MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();

        // Usuario de seguridad simulado
        usuarioSeguridad = new UsuarioSeguridad(1L, "testUser", "USER", null, null);
    }

    @Test
    public void testUpdateTransactionLimit_Success() throws Exception {
        // Datos de entrada
        Long accountId = 1L;
        Double newTransactionLimit = 5000.0;
        AccountDTO mockAccountDTO = new AccountDTO(1L, "cbu123", CurrencyTypeEnum.ARS, newTransactionLimit, 10000.0);

        // Comportamiento simulado de los servicios
        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(usuarioSeguridad);
        when(accountService.updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit)))
                .thenReturn(mockAccountDTO);

        // Realizar la petición PATCH
        mockMvc.perform(patch("/accounts/{id}", accountId)
                        .param("newTransactionLimit", newTransactionLimit.toString())
                        .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockAccountDTO.getId()))
                .andExpect(jsonPath("$.transactionLimit").value(mockAccountDTO.getTransactionLimit()));

        // Verificar que el servicio fue invocado
        verify(accountService, times(1)).updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit));
    }

    @Test
    public void testUpdateTransactionLimit_AccountNotFound() throws Exception {
        // Simulación de cuenta no encontrada
        Long accountId = 999L;
        Double newTransactionLimit = 5000.0;

        // Comportamiento simulado de los servicios
        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(usuarioSeguridad);
        when(accountService.updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit)))
                .thenThrow(new WalletsException(HttpStatus.NOT_FOUND, "Cuenta no encontrada."));

        // Realizar la petición PATCH y verificar que el status es NOT_FOUND
        mockMvc.perform(patch("/accounts/{id}", accountId)
                        .param("newTransactionLimit", newTransactionLimit.toString())
                        .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cuenta no encontrada."));

        // Verificar que el servicio fue invocado
        verify(accountService, times(1)).updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit));
    }

    @Test
    public void testUpdateTransactionLimit_InvalidTransactionLimit() throws Exception {
        // Datos de entrada inválidos
        Long accountId = 1L;
        Double newTransactionLimit = -5000.0;

        // Comportamiento simulado
        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(usuarioSeguridad);
        when(accountService.updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit)))
                .thenThrow(new WalletsException(HttpStatus.BAD_REQUEST, "El limite de transacción no puede ser nulo."));

        // Realizar la petición PATCH y verificar que el status es BAD_REQUEST
        mockMvc.perform(patch("/accounts/{id}", accountId)
                        .param("newTransactionLimit", newTransactionLimit.toString())
                        .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El limite de transacción no puede ser nulo."));

        // Verificar que el servicio fue invocado
        verify(accountService, times(1)).updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit));
    }

    @Test
    public void testUpdateTransactionLimit_Forbidden() throws Exception {
        // Datos de entrada
        Long accountId = 1L;
        Double newTransactionLimit = 5000.0;

        // Comportamiento simulado (usuario no autorizado)
        when(usuarioLoggeadoService.getInfoUserSecurity(any())).thenReturn(usuarioSeguridad);
        when(accountService.updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit)))
                .thenThrow(new WalletsException(HttpStatus.FORBIDDEN, "No esta autorizado para modificar esta cuenta."));

        // Realizar la petición PATCH y verificar que el status es FORBIDDEN
        mockMvc.perform(patch("/accounts/{id}", accountId)
                        .param("newTransactionLimit", newTransactionLimit.toString())
                        .header("Authorization", "Bearer mockToken"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("No esta autorizado para modificar esta cuenta."));

        // Verificar que el servicio fue invocado
        verify(accountService, times(1)).updateTransactionLimit(eq(accountId), eq(usuarioSeguridad.getId()), eq(newTransactionLimit));
    }
}
