package com.example.bbva.squad2.Wallet.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.example.bbva.squad2.Wallet.enums.RoleName;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@Entity

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false,updatable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false)
    private LocalDateTime updateDate;

    //se ejecuta antes de hacer el insert
    @PrePersist
    protected void onCreate(){
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    //se ejecuta antes de hacer un update en la bd
    @PreUpdate
    protected void onUpdate(){
        this.updateDate = LocalDateTime.now();
    }

}
