package com.example.bbva.squad2.Wallet.dtos;


import com.example.bbva.squad2.Wallet.enums.Concept;
import com.example.bbva.squad2.Wallet.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionDTO {
    private Concept concept;
    private String description;

    public static UpdateTransactionDTO fromTransaction(Transaction transaction) {
        UpdateTransactionDTO dto = new UpdateTransactionDTO();
        dto.setDescription(transaction.getDescription());
        dto.setConcept(transaction.getConcept());

        return dto;
    }
}