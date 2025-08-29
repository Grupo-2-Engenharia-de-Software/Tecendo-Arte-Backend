package com.crowdfunding.tecendoarte.models;

import java.util.*;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artistas")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_artista", nullable = false, unique = true)
    private Long id;

    @Column(name = "nome_artista", nullable = false, unique = true)
    private String nome;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_conta", referencedColumnName = "idConta", nullable = false, unique = true)
    private Conta conta;

    @Column(name = "descricao_artista", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Builder.Default
    @ElementCollection(targetClass = TipoArte.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "artista_categorias", joinColumns = @JoinColumn(name = "id_artista"))
    @Enumerated(EnumType.STRING)
    @Column(name = "categorias", nullable = false)
    private List<TipoArte> categorias = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "artista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Projeto> projetos = new ArrayList<>();

    // A modelagem indica a necessidade do atributo "recompensas". 
    // Não é necessário, pois são os projetos que possuem recompensas.

}
