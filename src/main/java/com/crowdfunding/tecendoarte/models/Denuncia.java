package com.crowdfunding.tecendoarte.models;

import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import com.crowdfunding.tecendoarte.models.enums.TipoDenuncia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDenuncia tipo;

    @Column(nullable = false)
    private Long referenciaId;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDenuncia status;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    @PrePersist
    void onCreate() {
        LocalDateTime agora = LocalDateTime.now();
        criadoEm = agora;
        atualizadoEm = agora;
        if (status == null) {
            status = StatusDenuncia.PENDENTE;
        }
    }

    @PreUpdate
    void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public void marcarComoProcedente() {
        if (status != StatusDenuncia.PENDENTE) {
            throw new IllegalArgumentException("Denúncia já foi analisada.");
        }
        status = StatusDenuncia.PROCEDENTE;
    }

    public void marcarComoImprocedente() {
        if (status != StatusDenuncia.PENDENTE) {
            throw new IllegalArgumentException("Denúncia já foi analisada.");
        }
        status = StatusDenuncia.IMPROCEDENTE;
    }
}
