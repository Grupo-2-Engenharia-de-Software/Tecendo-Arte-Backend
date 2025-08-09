package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoRequestDTO;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;

public interface ProjetoServiceInterface {
    ProjetoResponseDTO cadastraProjeto(ProjetoRequestDTO dto);
}