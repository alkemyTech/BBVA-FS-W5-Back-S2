package com.example.bbva.squad2.Wallet.controllers;

import com.example.bbva.squad2.Wallet.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TransactionService ts;

    /*@GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionListDTO> findTransactionById(@PathVariable Long id)*/





}
