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
    private List<SendTransactionDTO> history;
    private List<FixedTermDTO> fixedTerms;

    public void setAccountArs(AccountBalance accountArs) {
        this.accountArs = accountArs;
    }

    public void setAccountUsd(AccountBalance accountUsd) {
        this.accountUsd = accountUsd;
    }

    public void setHistory(List<SendTransactionDTO> history) {
        this.history = history;
    }

    public void setFixedTerms(List<FixedTermDTO> fixedTerms) {
        this.fixedTerms = fixedTerms;
    }

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
