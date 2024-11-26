package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_User_Id(Long userId);
}
