package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    }


