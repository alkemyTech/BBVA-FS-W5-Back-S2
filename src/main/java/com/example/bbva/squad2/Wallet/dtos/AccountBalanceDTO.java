package com.example.bbva.squad2.Wallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {
    private AccountBalance accountArs;
    private AccountBalance accountUsd;
    private List<TransactionDTO> history;
    private List<FixedTermDTO> fixedTerms;

    public void setAccountArs(AccountBalance accountArs) {
        this.accountArs = accountArs;
    }

    public void setAccountUsd(AccountBalance accountUsd) {
        this.accountUsd = accountUsd;
    }

    public void setHistory(List<TransactionDTO> history) {
        this.history = history;
    }

    public void setFixedTerms(List<FixedTermDTO> fixedTerms) {
        this.fixedTerms = fixedTerms;
    }

    public static class AccountBalance {
        private Double balance;
        private String currency;

        public AccountBalance(Double balance, String currency) {
            this.balance = balance;
            this.currency = currency;
        }
    }
}
