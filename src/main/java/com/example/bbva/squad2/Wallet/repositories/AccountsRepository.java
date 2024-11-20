package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountsRepository extends JpaRepository<Account, Long> {
 
}
