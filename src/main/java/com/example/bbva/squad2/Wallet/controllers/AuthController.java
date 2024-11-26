package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.AccountDTO;
import com.example.bbva.squad2.Wallet.dtos.LoginDTO;
import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.AuthService;
import com.example.bbva.squad2.Wallet.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService as;

    @Autowired
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO userDTO) {
        try {
            // Registrar usuario
            User createdUser = userService.registerUser(userDTO);
            AccountDTO accountDTO = as.createAccount(createdUser.getId(), CurrencyTypeEnum.ARS);

            //lo comente por que ya mapeo sino seria dos veces
            // Construir respuesta con datos visibles
//            RegisterDTO responseDTO = RegisterDTO.builder()
//                    .firstName(createdUser.getFirstName())
//                    .lastName(createdUser.getLastName())
//                    .email(createdUser.getEmail())
//                    .build();

            // Generar token de autenticación
            String token = authService.generateToken(createdUser);

            //Construir respuesta con token y datos del usuario
            Map<String, Object> response = Map.of(
                    "user", RegisterDTO.builder()
                            .firstName(createdUser.getFirstName())
                            .lastName(createdUser.getLastName())
                            .email(createdUser.getEmail())
                            .build(),
                    "token", token
            );

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Error registering user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // Llama al servicio de autenticación con los datos del DTO
            Map<String, Object> response = authService.login(loginDTO.getEmail(), loginDTO.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
