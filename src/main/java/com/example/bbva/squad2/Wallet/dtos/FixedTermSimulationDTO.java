package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermSimulationDTO extends FixedTermDTO {
    private double total;

    public FixedTermSimulationDTO mapFromFixedTerm(FixedTermDeposit fixedTermDeposit) {
        super.mapFromFixedTerm(fixedTermDeposit);
        this.total = fixedTermDeposit.getAmount() + fixedTermDeposit.getInterest();
        return this;
    }
}
