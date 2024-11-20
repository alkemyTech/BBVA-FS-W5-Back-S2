package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
import com.example.bbva.squad2.Wallet.models.User;
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
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO userDTO) {
        try {
            // Registrar usuario
            User createdUser = userService.registerUser(
                    userDTO.getFirstName(),
                    userDTO.getLastName(),
                    userDTO.getEmail(),
                    userDTO.getPassword()
            );

            // Construir respuesta con datos visibles
            RegisterDTO responseDTO = RegisterDTO.builder()
                    .firstName(createdUser.getFirstName())
                    .lastName(createdUser.getLastName())
                    .email(createdUser.getEmail())
                    .build();

            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Error registering user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        return ResponseEntity.ok(authService.login(username, password));
    }
}