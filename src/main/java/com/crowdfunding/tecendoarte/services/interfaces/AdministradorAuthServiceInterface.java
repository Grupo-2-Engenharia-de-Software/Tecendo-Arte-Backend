package com.crowdfunding.tecendoarte.services.interfaces;

import com.crowdfunding.tecendoarte.dto.AdministradorDTO.AdminLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.AdministradorDTO.AdminLoginResponseDTO;

public interface AdministradorAuthServiceInterface {
    AdminLoginResponseDTO login(AdminLoginRequestDTO request);
}
