package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionBalanceDTO {
    private String cbuDestino;

    private String cbuOrigen;

    private Double amount;

    private CurrencyTypeEnum currency;

    private String description;

    public TransactionBalanceDTO mapFromTransaction(Transaction transaction) {
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.cbuDestino = transaction.getCbuDestino();
        this.cbuOrigen = transaction.getCbuOrigen();
        this.currency = transaction.getAccount().getCurrency();
        return this;
    }
}
