package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFixedTermDTO {

    @NotNull(message = "Es necesario el monto")
    @Positive(message = "El monto debe ser mayor que cero")
    private Double amount;

    @NotNull(message = "Es necesaria la cantidad de dias")
    @Positive(message = "Los días deben ser mayores que cero")
    private Integer days;

    @NotEmpty(message = "Es necesaria la cuenta a debitar")
    private String cbu;
}
