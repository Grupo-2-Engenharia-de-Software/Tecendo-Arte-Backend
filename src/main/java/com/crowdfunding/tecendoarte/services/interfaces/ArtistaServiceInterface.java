package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginResponseDTO;

public interface ArtistaServiceInterface {

    public ArtistaResponseDTO cadastrarArtista(ArtistaRequestDTO dto);
    
    ArtistaLoginResponseDTO login(ArtistaLoginRequestDTO request);
}
