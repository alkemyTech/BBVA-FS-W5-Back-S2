package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.FixedTermDTO;
import com.example.bbva.squad2.Wallet.dtos.FixedTermSimulationDTO;
import com.example.bbva.squad2.Wallet.dtos.UsuarioSeguridad;
import com.example.bbva.squad2.Wallet.services.FixedTermDepositService;
import com.example.bbva.squad2.Wallet.services.UsuarioLoggeadoService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FixedTermDepositControllerTest {

    @InjectMocks
    private FixedTermDepositController fixedTermDepositController;

    @Mock
    private FixedTermDepositService fixedTermDepositService;

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
    void testCreateFixedTermDeposit_Success() {
        // Arrange
        Double amount = 10000.0;
        Integer days = 60;
        FixedTermSimulationDTO mockResponse = new FixedTermSimulationDTO();
        mockResponse.setAmount(amount);
        mockResponse.setStartDate("2024-12-01");
        mockResponse.setEndDate("2025-01-30");
        mockResponse.setInterestRate(5.0);
        mockResponse.setAccountCBU("123456789");

        when(usuarioLoggeadoService.getInfoUserSecurity(ArgumentMatchers.any(HttpServletRequest.class))).thenReturn(mockUser);
        when(fixedTermDepositService.createFixedTermDeposit(anyLong(), anyDouble(), anyInt(), anyBoolean()))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<FixedTermSimulationDTO> response = fixedTermDepositController.createFixedTermDeposit(amount, days, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        FixedTermDTO mockResponse3 = (FixedTermDTO) response.getBody();
        assertEquals(mockResponse, mockResponse3);
    }

    @Test
    void testCreateFixedTermDeposit_Error() {
        // Arrange
        Double amount = 500.0;
        Integer days = 25;
        String expectedErrorMessage = "El plazo fijo debe ser de al menos 30 días.";

        when(usuarioLoggeadoService.getInfoUserSecurity(any(HttpServletRequest.class))).thenReturn(mockUser);
        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(fixedTermDepositService).createFixedTermDeposit(anyLong(), anyDouble(), anyInt(), anyBoolean());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fixedTermDepositController.createFixedTermDeposit(amount, days, request);
        });

        assertEquals(expectedErrorMessage, exception.getMessage());
    }
}
