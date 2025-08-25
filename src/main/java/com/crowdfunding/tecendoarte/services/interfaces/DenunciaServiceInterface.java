package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.models.Denuncia;
import com.crowdfunding.tecendoarte.models.enums.StatusDenuncia;

import java.util.List;

public interface DenunciaServiceInterface {
    List<Denuncia> listarTodas();
    Denuncia analisar(Long id, StatusDenuncia resultado);
}
