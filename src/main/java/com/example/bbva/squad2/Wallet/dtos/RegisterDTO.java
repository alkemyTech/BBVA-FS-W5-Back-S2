package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @NotNull(message = "La cuenta es obligatorio")
    private List<AccountDTO> accounts;

    //cambiar a un solo rol
    @NotNull(message = "El rol es obligatorio")
    private Set <Role> roles;



    public RegisterDTO mapFromAccount(final User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();

        // Convertir las cuentas del usuario a AccountDTO
        this.accounts = user.getAccounts().stream()
                .map(account -> AccountDTO.builder()
                        .id(account.getId())
                        .currency(account.getCurrency())
                        .transactionLimit(account.getTransactionLimit())
                        .balance(account.getBalance())
                        .build())
                .collect(Collectors.toList());

        this.roles = user.getRoles();

        return this;
    }
}
