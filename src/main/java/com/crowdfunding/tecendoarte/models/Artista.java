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
    @Column(name = "id_artista")
    private Long id;

    @Column(name = "nome_artista", nullable = false)
    private String nome;

    @Column(name = "email_artista", nullable = false, unique = true)
    private String email;

    @Column(name = "senha_artista", nullable = false)
    private String senha;

    @Column(name = "confirmacao_senha", nullable = false)
    private String confirmacaoSenha;

    @Builder.Default
    @ElementCollection(targetClass = TipoArte.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "artista_tipos_arte", joinColumns = @JoinColumn(name = "id_artista"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipos_arte")
    private List<TipoArte> tiposArte = new ArrayList<>();

}
