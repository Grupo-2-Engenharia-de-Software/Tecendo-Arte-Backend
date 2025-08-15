package com.crowdfunding.tecendoarte.models;

import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import com.crowdfunding.tecendoarte.models.enums.TipoDenuncia;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDenuncia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDenuncia tipoDenuncia;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDenuncia statusDenuncia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta autor;

    @Column(nullable = false)
    private Long idAlvo;
}
