package com.example.bbva.squad2.Wallet.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @Email(message = "Debe ser un email valido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatorio")
    @Size(min= 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

}
