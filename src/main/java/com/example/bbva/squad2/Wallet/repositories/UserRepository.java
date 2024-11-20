package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
