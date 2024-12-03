package com.example.bbva.squad2.Wallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipientResponseDTO {
    private Long idRecipient;
    private String nombreApellido;
    private List<AccountDTO> accountsDTO = new ArrayList<>();;
    private String username;
    private String bancoWallet;

    public void addAccountDTO(AccountDTO accountDTO){
        this.accountsDTO.add(accountDTO);
    }
}

