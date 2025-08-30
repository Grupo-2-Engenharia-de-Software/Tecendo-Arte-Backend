package com.crowdfunding.tecendoarte.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInteracao;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)    
    @JoinColumn(name = "remetente_id", nullable = false)
    private Conta remetente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Conta destinatario;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "projeto_id")
    private Projeto projetoRelacionado;
}