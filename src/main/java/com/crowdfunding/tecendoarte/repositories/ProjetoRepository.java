package com.crowdfunding.tecendoarte.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crowdfunding.tecendoarte.models.Projeto;
import com.crowdfunding.tecendoarte.models.enums.StatusProjeto;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    List<Projeto> findByStatus(StatusProjeto status);
    List<Projeto> findByArtistaId(Long artistaId);
    List<Projeto> findByTipoArte(TipoArte tipoArte);

}
