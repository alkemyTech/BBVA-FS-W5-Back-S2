package com.example.bbva.squad2.Wallet.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    //@Pattern(regexp = "^.{8,}$\n", message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un signo especial.")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Getter
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Account> accounts;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "soft_delete")
    private LocalDateTime softDelete;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.softDelete == null;
    }

    public void softDelete() {
        this.softDelete = LocalDateTime.now();
    }

    public void restore() {
        this.softDelete = null;
    }
}
