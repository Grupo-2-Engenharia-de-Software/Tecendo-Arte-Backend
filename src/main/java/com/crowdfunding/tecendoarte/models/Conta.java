package com.crowdfunding.tecendoarte.models;


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
    private String confirmacaoSenha;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private TipoConta conta;
}