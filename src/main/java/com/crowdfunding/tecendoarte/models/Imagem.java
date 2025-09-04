package com.crowdfunding.tecendoarte.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImagem;

    @Lob
    @Column(name = "dados_imagem", nullable = false)
    private byte[] dadosImagem; // Conteúdo binário da imagem

    @Column(name = "descricao", length = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projeto")
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_artista")
    private Artista artista;
}