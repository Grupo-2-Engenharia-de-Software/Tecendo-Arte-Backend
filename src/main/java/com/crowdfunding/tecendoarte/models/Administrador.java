package com.crowdfunding.tecendoarte.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administradores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adm", nullable = false, unique = true)
    private Long idAdm;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @OneToMany(mappedBy = "administrador", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Denuncia> denunciasAnalisadas = new ArrayList<>();
}
