package com.example.bbva.squad2.Wallet.services;

import com.example.bbva.squad2.Wallet.models.Transaction;
import com.example.bbva.squad2.Wallet.repositories.TransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionsRepository transactionsRepository;

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionsRepository.findByAccount_User_Id(userId);
    }
}


