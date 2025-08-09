package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoRequestDTO;
import com.crowdfunding.tecendoarte.dto.ProjetoDTO.ProjetoResponseDTO;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.Projeto;
import com.crowdfunding.tecendoarte.models.enums.StatusProjeto;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ProjetoRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ProjetoServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProjetoService implements ProjetoServiceInterface {

    private final ProjetoRepository projetoRepository;
    private final ArtistaRepository artistaRepository;

    @Override
    @Transactional
    public ProjetoResponseDTO cadastraProjeto(ProjetoRequestDTO dto) {
        
        Artista artista = artistaRepository.findById(dto.getIdArtista())
                .orElseThrow(() -> new EntityNotFoundException("Artista não encontrado."));

        
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título do projeto é obrigatório.");
        }
        if (dto.getDescricaoProjeto() == null || dto.getDescricaoProjeto().isBlank()) {
            throw new IllegalArgumentException("Descrição do projeto é obrigatória.");
        }
        if (dto.getMeta() == null || dto.getMeta() <= 0) {
            throw new IllegalArgumentException("Meta do projeto deve ser maior que zero.");
        }
        if (dto.getTipoArte() == null) {
            throw new IllegalArgumentException("Tipo de arte é obrigatório.");
        }

        Projeto projeto = Projeto.builder()
                .titulo(dto.getTitulo())
                .descricaoProjeto(dto.getDescricaoProjeto())
                .meta(dto.getMeta())
                .tipoArte(dto.getTipoArte())
                .artista(artista)
                .status(StatusProjeto.AGUARDANDO_AVALIACAO)
                .dataCriacao(LocalDate.now())
                .valorArrecadado(0.0)
                .build();

        projeto = projetoRepository.save(projeto);

        return ProjetoResponseDTO.builder()
                .idProjeto(projeto.getIdProjeto())
                .titulo(projeto.getTitulo())
                .descricaoProjeto(projeto.getDescricaoProjeto())
                .meta(projeto.getMeta())
                .valorArrecadado(projeto.getValorArrecadado())
                .dataCriacao(projeto.getDataCriacao())
                .status(projeto.getStatus())
                .tipoArte(projeto.getTipoArte())
                .nomeArtista(projeto.getArtista() != null ? projeto.getArtista().getNome() : null)
                .build();
    }
}