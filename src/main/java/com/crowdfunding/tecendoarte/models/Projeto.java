package com.crowdfunding.tecendoarte.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.crowdfunding.tecendoarte.models.enums.StatusProjeto;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projetos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long idProjeto;
    
    @OneToMany(mappedBy = "projetoRelacionado", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<Interacao> interacoes = new java.util.ArrayList<>();

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descricao_projeto", nullable = false)
    private String descricaoProjeto;

    @Column(name = "meta", nullable = false)
    private Double meta;

    @Column(name = "valor_arrecadado")
    private Double valorArrecadado;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status;

    @Column(name = "descricao_recompensa", columnDefinition = "TEXT")
    private String descricaoRecompensa;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_validator")
    private Administrador validator;

    //Iremos guardar a URL da imagem
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Imagem> imagens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_arte", nullable = false)
    private TipoArte tipoArte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_artista", nullable = false)
    private Artista artista;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Doacao> doacoes = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Denuncia> denuncias = new ArrayList<>();
}

