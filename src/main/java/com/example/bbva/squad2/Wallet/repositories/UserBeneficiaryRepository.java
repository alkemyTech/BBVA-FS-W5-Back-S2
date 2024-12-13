package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.models.User;
import com.example.bbva.squad2.Wallet.models.UserBeneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBeneficiaryRepository extends JpaRepository<UserBeneficiary, Long> {
    boolean existsByUsuarioAndBeneficiarioAndCbu(User usuario, User beneficiario, String cbu);
}
