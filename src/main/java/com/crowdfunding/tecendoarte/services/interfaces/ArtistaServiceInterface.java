package com.crowdfunding.tecendoarte.services.interfaces;

import java.util.List;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ArtistaDTO.ArtistaResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;

public interface ArtistaServiceInterface {

    public Artista cadastrarArtista(ArtistaRequestDTO dto);
    public ArtistaResponseDTO consultarArtista(String nome);
    public List<ArtistaResponseDTO> listarArtistas();
    public Artista atualizarArtista(String nome, ArtistaRequestDTO dto);
    public void deletarArtista(String nome);
    
}
