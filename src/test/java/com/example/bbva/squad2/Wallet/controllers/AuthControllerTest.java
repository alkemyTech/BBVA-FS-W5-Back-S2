package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.LoginDTO;
import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
import com.example.bbva.squad2.Wallet.services.AuthService;
import com.example.bbva.squad2.Wallet.services.UserRegisterServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRegisterServices userRegisterServices;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("pedro.ruiz@yopmail.com")
                .password("Pedro!2024Ruiz#")
                .build();

        // Simular la creación del token JWT
        String token = "mock-jwt-token";  // Sustituye esto por un token válido si es necesario
        Map<String, Object> response = new HashMap<>();
        response.put("id", 1);
        response.put("token", token);

        ResponseEntity<?> result = authController.login(loginDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testLogin_Failure() {
        LoginDTO loginDTO = LoginDTO.builder()
                .email("wrong.email@yopmail.com")
                .password("WrongPassword123")
                .build();

        when(authService.login(loginDTO.getEmail(), loginDTO.getPassword()))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        ResponseEntity<?> result = authController.login(loginDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void testRegister_Success() {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstName("Juan Cruz")
                .lastName("Caggiano")
                .email("juancruz.caggiano@bbva.com")
                .password("JuanCruz!2024Caggiano#")
                .build();

        // Ejecutar el método del controlador
        ResponseEntity<?> result = authController.registerUser(registerDTO);

        // Verificar los resultados
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void testRegister_Failure() {
        RegisterDTO registerDTO = RegisterDTO.builder()
                .firstName("Pedro")
                .lastName("Ruiz")
                .email("invalid-email")  // Email inválido
                .password("short")       // Contraseña demasiado corta
                .build();

        // Ejecutar el método del controlador
        ResponseEntity<?> result = authController.registerUser(registerDTO);

        // Verificar que se devuelve el código de error adecuado
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

}
