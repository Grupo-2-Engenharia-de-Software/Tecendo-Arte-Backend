package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaLoginResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;

public interface ArtistaServiceInterface {

    public Artista cadastrarArtista(ArtistaRequestDTO dto);
    
    ArtistaLoginResponseDTO login(ArtistaLoginRequestDTO request);
}
