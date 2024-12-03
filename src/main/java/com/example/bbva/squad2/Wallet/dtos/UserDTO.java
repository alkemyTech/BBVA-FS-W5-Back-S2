package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.Role;
import com.example.bbva.squad2.Wallet.models.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;



@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserDTO {
    public Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private List<AccountDTO> accounts;

    public static UserDTO mapFromUser(final User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .accounts(user.getAccounts().stream()
                        .map(account -> new AccountDTO().mapFromAccount(account))
                        .collect(Collectors.toList()))
                .build();
    }

}
