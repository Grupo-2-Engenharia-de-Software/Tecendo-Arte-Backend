package com.crowdfunding.tecendoarte.models;

import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList; // se inicializar as listas
import com.crowdfunding.tecendoarte.models.Interacao;
import com.crowdfunding.tecendoarte.models.Denuncia;
import com.crowdfunding.tecendoarte.models.Comentario;
import com.crowdfunding.tecendoarte.models.Doacao;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long idConta;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipoConta;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Denuncia> denunciasFeitas = new ArrayList<>();

    @OneToMany(mappedBy = "remetente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Interacao> interacoesEnviadas = new ArrayList<>();

    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Interacao> interacoesRecebidas = new ArrayList<>();

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comentario> comentariosFeitos = new ArrayList<>();

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Doacao> doacoes = new ArrayList<>();
}