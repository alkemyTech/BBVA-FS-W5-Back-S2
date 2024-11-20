package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import com.example.bbva.squad2.Wallet.services.FixedTermDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fixed-term-deposits")
public class FixedTermDepositController {

    @Autowired
    private final FixedTermDepositService fixedTermDepositService;

    public FixedTermDepositController(FixedTermDepositService fixedTermDepositService) {
        this.fixedTermDepositService = fixedTermDepositService;
    }

    @GetMapping
    public ResponseEntity<List<FixedTermDeposit>> getAllFixedTermDeposits() {
        List<FixedTermDeposit> deposits = fixedTermDepositService.getAllFixedTermDeposits();
        return ResponseEntity.ok(deposits);
    }
}
