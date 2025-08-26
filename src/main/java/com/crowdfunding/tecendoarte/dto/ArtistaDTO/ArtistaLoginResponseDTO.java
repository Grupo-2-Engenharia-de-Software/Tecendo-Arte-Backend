package com.crowdfunding.tecendoarte.dto.ArtistaDTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaLoginResponseDTO {

    private String nome;
    private String email;
    private String token;

}
