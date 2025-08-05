package com.crowdfunding.tecendoarte.dto.UsuarioDTO;

import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private Long contaId;
    private List<TipoArte> interesses;
}
