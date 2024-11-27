package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor


public class TransactionListDTO {

    private Long id;
    private String cbuDestino;
    private String cbuOrigen;
    private Double amount;
    private TransactionTypeEnum type;
    private String description;
    private LocalDateTime timestamp;

    // Método para mapear desde la entidad Transaction
    public static TransactionListDTO fromEntity(Transaction transaction) {
        return TransactionListDTO.builder()
                .id(transaction.getId())
                .cbuDestino(transaction.getCbuDestino())
                .cbuOrigen(transaction.getCbuOrigen())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .timestamp(transaction.getTimestamp())
                .build();
    }

}
