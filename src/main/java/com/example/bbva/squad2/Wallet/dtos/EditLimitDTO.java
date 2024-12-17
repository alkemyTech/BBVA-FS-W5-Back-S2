package com.example.bbva.squad2.Wallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditLimitDTO {
    private Long accountId;
    private Double newTransactionLimit;
}
