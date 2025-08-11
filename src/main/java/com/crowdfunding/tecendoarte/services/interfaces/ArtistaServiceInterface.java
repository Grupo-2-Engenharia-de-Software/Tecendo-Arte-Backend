package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;

public interface ArtistaServiceInterface {

    public Artista cadastrarArtista(ArtistaRequestDTO dto);
    public ArtistaResponseDTO consultarArtista(String nome);
    
}
