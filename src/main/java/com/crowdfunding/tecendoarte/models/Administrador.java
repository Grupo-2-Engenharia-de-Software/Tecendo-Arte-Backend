package com.crowdfunding.tecendoarte.models;

import com.crowdfunding.tecendoarte.models.enums.PermissaoAdministrador;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "administrador_permissao", joinColumns = @JoinColumn(name = "administrador_id"))
    @Column(name = "permissao", nullable = false)
    @Builder.Default
    private Set<PermissaoAdministrador> permissoes = new HashSet<>();
}
