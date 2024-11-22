package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.Transaction;

public class TransactionDTO {
    private String description;
    private Double amount;
    private String date;
    private String type; // 'income' o 'payment'

    public TransactionDTO mapFromTransaction(Transaction transaction) {
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.date = transaction.getTimestamp().toString();
        this.type = transaction.getType().name();
        return this;
    }

}
