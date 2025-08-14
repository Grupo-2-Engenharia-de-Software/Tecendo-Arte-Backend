package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.ContaDTO.*;

public interface ContaServiceInterface {
    ContaResponseDTO cadastrar(ContaRequestDTO conta);

    ContaResponseDTO atualizarConta(Long id, ContaRequestDTO dto);
    
    ContaResponseDTO buscarPorId(Long id);

    boolean excluirConta(Long id);

}