package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioResponseDTO;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Usuario;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
import com.crowdfunding.tecendoarte.services.interfaces.UsuarioServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioServiceInterface {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;

    @Override
    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        Conta conta = contaRepository.findById(dto.getContaId())
                .orElseThrow(() -> new EntityNotFoundException("Conta nao encontrada"));

        Usuario usuario = Usuario.builder()
                .conta(conta)
                .interesses(dto.getInteresses())
                .build();

        usuarioRepository.save(usuario);

        return toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));
        return toResponseDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario nao encontrado"));

        usuario.setInteresses(dto.getInteresses());

        return toResponseDTO(usuario);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario nao encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setContaId(usuario.getConta().getIdConta());
        dto.setInteresses(usuario.getInteresses());
        return dto;
    }
}
