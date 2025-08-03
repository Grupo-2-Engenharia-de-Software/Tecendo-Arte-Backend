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
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "conta_id", nullable = false, unique = true)
    private Conta conta;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_interesse", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<TipoArte> interesses = new ArrayList<>();
}