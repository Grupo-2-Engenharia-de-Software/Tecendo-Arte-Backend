package com.crowdfunding.tecendoarte.dto.AdministradorDTO;

import java.util.List;
import com.crowdfunding.tecendoarte.dto.ContaDTO.ContaResponseDTO;
import com.crowdfunding.tecendoarte.dto.DenunciaDTO.DenunciaResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorResponseDTO {
    
    @JsonProperty("idAdm")
    private Long idAdm;

    @JsonProperty("conta")
    private ContaResponseDTO conta;

    @JsonProperty("denunciasAnalisadas")
    private List<DenunciaResponseDTO> denunciasAnalisadas;
}