package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.LoginDTO;
import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.AuthService;
import com.example.bbva.squad2.Wallet.services.UserRegisterServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthControllerIntegrateTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private AccountService accountService;

    @Mock
    private UserRegisterServices userRegisterServices;

    private RegisterDTO mockRegisterDTO;
    private LoginDTO mockLoginDTO;  // Asegúrate de que el nombre aquí es 'mockLoginDTO'

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Usando el constructor con parámetros generado por Lombok
        mockRegisterDTO = new RegisterDTO(
                "Test",
                "User",
                "test@example.com",
                "Password123!"
        );

        mockLoginDTO = new LoginDTO(
                "test@example.com",
                "Password123!"
        );
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        Map<String, Object> mockResponse = Map.of("message", "Usuario registrado exitosamente");

        when(userRegisterServices.registerUser(ArgumentMatchers.any(RegisterDTO.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = authController.registerUser(mockRegisterDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }
}
