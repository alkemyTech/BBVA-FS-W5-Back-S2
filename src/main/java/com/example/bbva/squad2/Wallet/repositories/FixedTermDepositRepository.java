package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.models.FixedTermDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedTermDepositRepository extends JpaRepository<FixedTermDeposit, Long> {

}
