package com.example.bbva.squad2.Wallet.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "usuario_beneficiarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBeneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = true)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiario_id", nullable = true)
    private User beneficiario;

    @NotNull
    @Column(name = "cbu", nullable = false)
    private String cbu;
}
