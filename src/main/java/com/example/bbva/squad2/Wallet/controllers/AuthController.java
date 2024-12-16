package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.LoginDTO;
import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
import com.example.bbva.squad2.Wallet.services.AccountService;
import com.example.bbva.squad2.Wallet.services.AuthService;
import com.example.bbva.squad2.Wallet.services.UserRegisterServices;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
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
    private final UserRegisterServices userRegisterServices;

    public AuthController(UserRegisterServices userRegisterServices) {

        this.userRegisterServices = userRegisterServices;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevos usuarios")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDTO userDTO) throws MessagingException {
        Map<String, Object> response = userRegisterServices.registerUser(userDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuarios ya creados")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> response = authService.login(loginDTO.getEmail(), loginDTO.getPassword());
        return ResponseEntity.ok(response);
    }
}
