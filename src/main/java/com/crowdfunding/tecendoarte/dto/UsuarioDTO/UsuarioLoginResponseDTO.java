package com.crowdfunding.tecendoarte.dto.UsuarioDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLoginResponseDTO {
    private String token;
    private String email;
    private String nome;
    private Long usuarioId;
    private String tipoConta;
}
