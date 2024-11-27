package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendPaymentDTO {
    @NotNull(message = "El nro de tarjeta no puede ser nulo")
    private String nroTarjeta;

    @NotNull(message = "El monto no puede ser nulo")
    private Double amount;

    @NotNull(message = "La moneda de la transferencia no puede estar vacía")
    private CurrencyTypeEnum currency;

    private String description;
}
