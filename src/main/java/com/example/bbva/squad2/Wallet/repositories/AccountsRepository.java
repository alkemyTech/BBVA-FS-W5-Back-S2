package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;
import com.example.bbva.squad2.Wallet.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountsRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCurrencyAndUser_Email(CurrencyTypeEnum currency, String email);
    Optional<Account> findByCbuAndCurrency(String cbu, CurrencyTypeEnum currency);
    Optional<Account> findByUserIdAndCurrency(Long userId, CurrencyTypeEnum currency);
    Optional<Account> findByCbu(String cbu);
    List<Account> findByUserId(Long userId);


}
