package com.example.bbva.squad2.Wallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bbva.squad2.Wallet.models.Accounts;

@Repository
public interface AccountsRepository extends JpaRepository<Accounts, Long> {
 
}
