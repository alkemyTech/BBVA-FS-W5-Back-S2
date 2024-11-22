package com.example.bbva.squad2.Wallet.dtos;

import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;

public class FixedTermDTO {
    private Double amount;
    private String startDate;
    private String endDate;
    private Double interestRate;

    public FixedTermDTO mapFromFixedTerm(FixedTermDeposit fixedTermDeposit) {
        this.amount = fixedTermDeposit.getAmount();
        this.startDate = fixedTermDeposit.getStartDate().toString();
        this.endDate = fixedTermDeposit.getEndDate() != null ? fixedTermDeposit.getEndDate().toString() : "N/A";
        this.interestRate = fixedTermDeposit.getInterestRate();
        return this;
    }
}
