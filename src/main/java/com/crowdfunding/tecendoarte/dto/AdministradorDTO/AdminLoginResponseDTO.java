package com.crowdfunding.tecendoarte.dto.AdministradorDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLoginResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String token;
}