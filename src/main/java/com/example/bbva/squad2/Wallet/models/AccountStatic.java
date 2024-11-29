package com.example.bbva.squad2.Wallet.models;

import lombok.Getter;
import lombok.Setter;

public class AccountStatic {

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
