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
public class FixedTermSimulationDTO {
    private Double amount;
    private String startDate;
    private String endDate;
    private Double interestRate;
    private String accountCBU;

    public FixedTermSimulationDTO mapFromFixedTerm(FixedTermDeposit fixedTermDeposit) {
        this.amount = fixedTermDeposit.getAmount();
        this.startDate = fixedTermDeposit.getStartDate().toString();
        this.endDate = fixedTermDeposit.getEndDate() != null ? fixedTermDeposit.getEndDate().toString() : "N/A";
        this.interestRate = fixedTermDeposit.getInterestRate();
        this.accountCBU = fixedTermDeposit.getAccount().getCbu();
        return this;
    }
}
