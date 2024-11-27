package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.dtos.TransactionBalanceDTO;
import com.example.bbva.squad2.Wallet.dtos.TransactionListDTO;
import com.example.bbva.squad2.Wallet.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TransactionService ts;

    /*@GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionListDTO> findTransactionById(@PathVariable Long id)*/





}
