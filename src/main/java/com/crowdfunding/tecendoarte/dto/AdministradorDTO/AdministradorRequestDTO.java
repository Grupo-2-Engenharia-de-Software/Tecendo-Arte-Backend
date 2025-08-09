package com.crowdfunding.tecendoarte.dto.AdministradorDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorRequestDTO {
        
    @JsonProperty("contaId")
    private Long contaId; 
}