package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import com.example.bbva.squad2.Wallet.repositories.FixedTermDepositRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixedTermDepositService {

    private final FixedTermDepositRepository fixedTermDepositRepository;

    public FixedTermDepositService(FixedTermDepositRepository fixedTermDepositRepository) {
        this.fixedTermDepositRepository = fixedTermDepositRepository;
    }

    public List<FixedTermDeposit> getAllFixedTermDeposits() {
        return fixedTermDepositRepository.findAll();
    }
}
