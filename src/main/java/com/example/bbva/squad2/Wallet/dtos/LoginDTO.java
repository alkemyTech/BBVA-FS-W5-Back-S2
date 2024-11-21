package com.example.bbva.squad2.Wallet.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotEmpty(message = "Es necesario el email")
    @Email(message = "Formato inválido")
    private String email;

    @NotEmpty(message = "Es necesarioa la contraseña")
    private String password;
}
