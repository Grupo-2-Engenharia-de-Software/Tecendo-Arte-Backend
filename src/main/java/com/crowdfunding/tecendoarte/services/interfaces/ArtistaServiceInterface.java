package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.ArtistaDTO.*;

import java.util.List;

public interface ArtistaServiceInterface {

    public ArtistaResponseDTO cadastrarArtista(ArtistaRequestDTO request);
    public ArtistaLoginResponseDTO login(ArtistaLoginRequestDTO request);
    public ArtistaResponseDTO buscarPorNome(String nome);
    public List<ArtistaResponseDTO> listarArtistas();
    public ArtistaResponseDTO atualizarArtista(String nome, ArtistaRequestDTO request);
    public void deletarArtista(String nome);

}