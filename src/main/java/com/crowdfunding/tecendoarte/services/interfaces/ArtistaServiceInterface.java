package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginResponseDTO;

import java.util.List;

public interface ArtistaServiceInterface {

    public ArtistaResponseDTO cadastrarArtista(ArtistaRequestDTO dto);
    
    ArtistaLoginResponseDTO login(ArtistaLoginRequestDTO request);

    List<ArtistaResponseDTO> buscarPorNome(String nome);
}
