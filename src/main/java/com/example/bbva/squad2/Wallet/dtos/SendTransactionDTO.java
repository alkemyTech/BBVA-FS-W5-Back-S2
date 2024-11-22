package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.models.Transaction;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendTransactionDTO {
    @NotNull(message = "El CBU de la cuenta destinataria no puede estar vacío")
    private String destinationCbu;

    @NotNull(message = "El monto no puede ser nulo")
    private Double amount;

    @NotNull(message = "La moneda de la transferencia no puede estar vacía")
    private CurrencyTypeEnum currency;

    private String description;

    public SendTransactionDTO mapFromTransaction(Transaction transaction) {
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.destinationCbu = transaction.getAccount().getCbu();
        this.currency = transaction.getAccount().getCurrency();
        return this;
    }
}
