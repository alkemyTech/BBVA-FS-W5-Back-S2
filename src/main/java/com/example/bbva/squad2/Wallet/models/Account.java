package com.example.bbva.squad2.Wallet.models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.bbva.squad2.Wallet.enums.CurrencyTypeEnum;

import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Accounts")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private CurrencyTypeEnum currency;

    @NotNull
    private Double transactionLimit;

    @NotNull
    private Double balance;
    
    //comentar esto que estaba asi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    @Column(updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

 
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

  
    public void restore() {
        this.deletedAt = null;
    }

    public boolean isActive() {
        return this.deletedAt == null;
    }

	
    
    
}


