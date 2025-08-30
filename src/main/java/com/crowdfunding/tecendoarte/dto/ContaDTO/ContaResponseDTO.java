package com.crowdfunding.tecendoarte.dto.ContaDTO;

import com.crowdfunding.tecendoarte.models.enums.TipoConta;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaResponseDTO {
    private Long idConta;
    private String nome;
    private String email;
    private TipoConta tipoConta;
}