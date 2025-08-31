package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioResponseDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioLoginRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioLoginResponseDTO;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Usuario;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
import com.crowdfunding.tecendoarte.services.interfaces.UsuarioServiceInterface;
import com.crowdfunding.tecendoarte.config.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UsuarioServiceInterface {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        Conta conta = contaRepository.findById(dto.getContaId())
                .orElseThrow(() -> new EntityNotFoundException("Conta nao encontrada"));

        // valida se já existe usuário com essa conta
        if (usuarioRepository.findByConta(conta).isPresent()) {
            throw new IllegalArgumentException("Ja existe um usuario vinculado a essa conta.");
        }

        Usuario usuario = Usuario.builder()
                .conta(conta)
                .interesses(dto.getInteresses())
                .build();

        usuario = usuarioRepository.save(usuario);

        return toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
        return toResponseDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));

        Conta conta = contaRepository.findById(dto.getContaId())
                .orElseThrow(() -> new IllegalArgumentException("ID da conta inválido"));

        usuario.setConta(conta);
        usuario.setInteresses(dto.getInteresses());

        usuarioRepository.save(usuario);

        return toResponseDTO(usuario);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UsuarioLoginResponseDTO login(UsuarioLoginRequestDTO dto) {
        Conta conta = contaRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getSenha(), conta.getSenha())) {
            throw new IllegalArgumentException("Senha incorreta");
        }

        Usuario usuario = usuarioRepository.findByConta(conta)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String token = jwtUtil.generateTokenForUsuario(usuario.getId(), conta.getEmail());

        return new UsuarioLoginResponseDTO(
                token,
                conta.getEmail(),
                conta.getNome(),
                usuario.getId(),
                conta.getTipoConta().toString()
        );
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setContaId(usuario.getConta().getIdConta());
        dto.setInteresses(usuario.getInteresses());
        return dto;
    }
}
