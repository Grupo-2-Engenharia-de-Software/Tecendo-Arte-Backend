package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioResponseDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioLoginResponseDTO;

public interface UsuarioServiceInterface {
    UsuarioResponseDTO criar(UsuarioRequestDTO dto);

    UsuarioResponseDTO buscarPorId(Long id);

    UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto);

    void deletar(Long id);
    
    UsuarioLoginResponseDTO login(UsuarioLoginRequestDTO dto);
}
