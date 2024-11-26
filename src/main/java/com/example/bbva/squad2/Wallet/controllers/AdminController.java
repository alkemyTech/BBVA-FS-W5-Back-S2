package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.TransactionBalanceDTO;
import com.example.bbva.squad2.Wallet.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AdminController {

    @Autowired
    private TransactionService ts;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionBalanceDTO> getTransaction(@PathVariable Long id) {
        Optional<TransactionBalanceDTO> transaction = ts.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
