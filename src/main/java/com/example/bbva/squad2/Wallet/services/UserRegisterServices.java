package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.dtos.RegisterDTO;
import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserRegisterServices {

    @Autowired
    private final UserService userService;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final AccountService as;
    @Autowired
    private EmailService emailService;

    public UserRegisterServices(UserService userService, AuthService authService, AccountService as) {
        this.userService = userService;
        this.authService = authService;
        this.as = as;
    }

    public Map<String, Object> registerUser(RegisterDTO userDTO) throws MessagingException {
        // Registrar usuario
        User createdUser = userService.registerUser(userDTO);

        // Crear cuenta
        as.createAccount(createdUser.getId(), CurrencyTypeEnum.ARS);
        as.createAccount(createdUser.getId(), CurrencyTypeEnum.USD);

        emailService.sendEmail(
                createdUser.getEmail(),
                "Usuario creado con éxito",
                "welcome",
                Map.of(
                        "firstName", createdUser.getFirstName(),
                        "lastName", createdUser.getLastName()
                )
        );

        // Generar token de autenticación
        String token = authService.generateToken(createdUser);

        // Construir respuesta
        return Map.of(
                "user", RegisterDTO.builder()
                        .firstName(createdUser.getFirstName())
                        .lastName(createdUser.getLastName())
                        .email(createdUser.getEmail())
                        .build(),
                "token", token
        );
    }
}
