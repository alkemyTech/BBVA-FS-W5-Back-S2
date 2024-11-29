package com.example.bbva.squad2.Wallet.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fixed_term_deposits")
public class FixedTermDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private Double interest;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "closing_date", nullable = true)
    private LocalDateTime closingDate;

    private Boolean processed = false;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.closingDate = LocalDateTime.now();
    }


    // agregue ful 34
    // Métodos getter adicionales

    public Double getAmount() {
        return amount;
    }

    public LocalDateTime getStartDate() {
        return creationDate;
    }

    public LocalDateTime getEndDate() {
        return closingDate;
    }

    public Double getInterestRate() {
        return interest;
    }
}
