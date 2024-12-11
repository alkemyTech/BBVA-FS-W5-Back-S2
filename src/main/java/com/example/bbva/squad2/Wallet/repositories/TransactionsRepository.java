package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccount_User_Id(Long userId);
    List<Transaction> findByAccount_User_IdOrderByTimestampDesc(Long userId);
    List<Transaction> findByAccount_Id(Long accountId);
    Optional<Transaction> findByIdAndAccount_User_Id(Long transactionId, Long userId);

    Page<Transaction> findByAccount_User_Id(Long userId, Pageable pageable);
}
