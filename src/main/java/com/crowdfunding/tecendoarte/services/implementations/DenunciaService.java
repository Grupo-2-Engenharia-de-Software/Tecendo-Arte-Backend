package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.models.Denuncia;
import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;
import com.crowdfunding.tecendoarte.repositories.DenunciaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.DenunciaServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DenunciaService implements DenunciaServiceInterface {

    private final DenunciaRepository denunciaRepository;

    @Override
    public List<Denuncia> listarTodas() {
        return denunciaRepository.findAll(Sort.by(Sort.Direction.DESC, "criadoEm"));
    }

    @Override
    public Denuncia analisar(Long id, StatusDenuncia resultado) {
        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada."));

        if (resultado == null) {
            throw new IllegalArgumentException("Resultado da análise é obrigatório.");
        }
        if (resultado == StatusDenuncia.PENDENTE) {
            throw new IllegalArgumentException("Resultado inválido para análise.");
        }

        switch (resultado) {
            case PROCEDENTE -> denuncia.marcarComoProcedente();
            case IMPROCEDENTE -> denuncia.marcarComoImprocedente();
            default -> throw new IllegalArgumentException("Resultado inválido para análise.");
        }

        return denunciaRepository.save(denuncia);
    }
}
