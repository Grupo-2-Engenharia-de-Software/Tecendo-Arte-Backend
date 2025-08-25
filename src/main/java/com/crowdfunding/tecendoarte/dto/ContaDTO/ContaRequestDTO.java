package com.crowdfunding.tecendoarte.dto.ContaDTO;

import com.crowdfunding.tecendoarte.models.enums.TipoConta;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaRequestDTO {
    private String nome;
    private String email;
    private String senha;
    private TipoConta tipoConta;
}