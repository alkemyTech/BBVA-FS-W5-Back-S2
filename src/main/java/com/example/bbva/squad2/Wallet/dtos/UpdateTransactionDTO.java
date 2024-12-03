package com.example.bbva.squad2.Wallet.dtos;


import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionDTO {
    private String cbuDestino;
    private String cbuOrigen;
    private Double amount;
    private CurrencyTypeEnum currency;
    private String description;

    public static UpdateTransactionDTO fromTransaction(Transaction transaction) {
        UpdateTransactionDTO dto = new UpdateTransactionDTO();
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setCbuDestino(transaction.getCbuDestino());
        dto.setCbuOrigen(transaction.getCbuOrigen());
        dto.setCurrency(transaction.getAccount().getCurrency());
        return dto;
    }
}