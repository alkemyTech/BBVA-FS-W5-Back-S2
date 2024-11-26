package com.example.bbva.squad2.Wallet.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendDepositDTO {
    private double amount;         // El monto que se quiere depositar
    private String description;    // Descripción del depósito
}
