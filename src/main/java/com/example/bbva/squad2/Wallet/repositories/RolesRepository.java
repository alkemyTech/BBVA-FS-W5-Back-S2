package com.example.bbva.squad2.Wallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bbva.squad2.Wallet.models.Role;

@RequestMapping
public interface RolesRepository extends JpaRepository<Role, Long> {
}
