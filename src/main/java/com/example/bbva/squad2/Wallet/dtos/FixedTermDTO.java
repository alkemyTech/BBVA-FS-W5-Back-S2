package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermDTO {
    private Double amount;
    private String startDate;
    private String endDate;
    private Double interestRate;
    private String accountCBU;
    private Boolean processed;

    public FixedTermDTO mapFromFixedTerm(FixedTermDeposit fixedTermDeposit) {
        this.amount = fixedTermDeposit.getAmount();
        this.startDate = fixedTermDeposit.getStartDate().toString();
        this.endDate = fixedTermDeposit.getEndDate() != null ? fixedTermDeposit.getEndDate().toString() : "N/A";
        this.interestRate = fixedTermDeposit.getInterestRate();
        this.accountCBU = fixedTermDeposit.getAccount().getCbu();
        this.processed = fixedTermDeposit.getProcessed();
        return this;
    }
}
