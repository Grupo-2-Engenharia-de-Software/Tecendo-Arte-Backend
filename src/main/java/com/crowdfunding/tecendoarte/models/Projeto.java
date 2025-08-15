package com.crowdfunding.tecendoarte.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

//DEVE SER IMPLEMENTADA AINDA
public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long idProjeto;
    
    @OneToMany(mappedBy = "projetoRelacionado", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<Interacao> interacoes = new java.util.ArrayList<>();
}

