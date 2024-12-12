package com.example.bbva.squad2.Wallet.dtos;


import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountNameDTO {
    private String firstName;
    private String lastName;
    private CurrencyTypeEnum currency;


    public static AccountNameDTO fromEntity(Account account) {
        return AccountNameDTO.builder()
                .firstName(account.getUser().getFirstName())  // Asegúrate de que `account.getUser()` no sea null
                .lastName(account.getUser().getLastName())
                .currency(account.getCurrency())// Similar para el apellido
                .build();

    }

}
