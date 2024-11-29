package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.AccountStatic;
import lombok.*;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    private AccountStatic.AccountBalance accountArs;
    private AccountStatic.AccountBalance accountUsd;
    private List<TransactionBalanceDTO> history;
    private List<FixedTermDTO> fixedTerms;

}
