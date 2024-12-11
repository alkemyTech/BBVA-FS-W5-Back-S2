package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.Concept;
import com.example.bbva.squad2.Wallet.enums.TransactionTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;
import com.example.bbva.squad2.Wallet.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
    private Concept concept;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalDateTime timestamp;
    private AccountNameDTO account;

    public static TransactionListDTO fromEntity(Transaction transaction) {
        return TransactionListDTO.builder()
                .id(transaction.getId())
                .cbuDestino(transaction.getCbuDestino())
                .cbuOrigen(transaction.getCbuOrigen())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .concept(transaction.getConcept())
                .timestamp(transaction.getTimestamp())
                .account(AccountNameDTO.fromEntity(transaction.getAccount()))
                .build();
    }

}
