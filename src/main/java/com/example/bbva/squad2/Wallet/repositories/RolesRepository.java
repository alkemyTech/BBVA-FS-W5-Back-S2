package com.example.bbva.squad2.Wallet.repositories;

import com.example.bbva.squad2.Wallet.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bbva.squad2.Wallet.models.Role;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Role, Long> {
    Optional<Role>findByName(RoleName name);
}
