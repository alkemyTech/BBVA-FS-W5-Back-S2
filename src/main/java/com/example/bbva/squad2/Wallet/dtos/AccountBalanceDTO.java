package com.example.bbva.squad2.Wallet.dtos;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    private AccountBalance accountArs;
    private AccountBalance accountUsd;
    private List<TransactionBalanceDTO> history;
    private List<FixedTermDTO> fixedTerms;


    @Getter
    @Setter
    public static class AccountBalance {
        private Double balance;
        private String currency;

        public AccountBalance(Double balance, String currency) {
            this.balance = balance;
            this.currency = currency;
        }
    }
}
