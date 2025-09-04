package com.crowdfunding.tecendoarte.dto.ImagemDTO;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagemRequestDTO {
    private String dadosImagemBase64; // Conteúdo da imagem em base64
    private String descricao;
    private Long projetoId;
}