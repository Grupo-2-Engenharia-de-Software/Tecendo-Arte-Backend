package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistaLoginResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String token;
} 