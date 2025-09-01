package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.dto.ContaDTO.*;
import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Usuario;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ContaServiceInterface;

import java.util.ArrayList;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaService implements ContaServiceInterface {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArtistaRepository artistaRepository;
    private final PasswordEncoder passwordEncoder;

    public ContaService(
            ContaRepository contaRepository,
            PasswordEncoder passwordEncoder,
            UsuarioRepository usuarioRepository,
            ArtistaRepository artistaRepository) {
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.artistaRepository = artistaRepository;
    }

    @Override
    @Transactional
    public ContaResponseDTO cadastrar(ContaRequestDTO contaDTO) {
        if (contaRepository.existsByEmail(contaDTO.getEmail())) {
            throw new IllegalArgumentException("Já existe uma conta com esse email.");
        }

        Conta conta = Conta.builder()
                .email(contaDTO.getEmail())
                .senha(passwordEncoder.encode(contaDTO.getSenha()))
                .nome(contaDTO.getNome())
                .tipoConta(contaDTO.getTipoConta())
                .build();

        Conta contaSalva = contaRepository.save(conta);

        //cria entidade associada conforme tipo de conta
        if (contaSalva.getTipoConta() == TipoConta.USUARIO) {
            Usuario usuario = Usuario.builder()
                    .conta(contaSalva)
                    .interesses(new ArrayList<>())
                    .build();
            usuarioRepository.save(usuario);
        } else if (contaSalva.getTipoConta() == TipoConta.ARTISTA) {
            Artista artista = Artista.builder()
                    .conta(contaSalva)
                    .build();
            artistaRepository.save(artista);
        }

        return toResponseDTO(contaSalva);
    }

    @Override
    @Transactional
    public ContaResponseDTO atualizarConta(Long id, ContaRequestDTO contaDTO) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada."));

        conta.setNome(contaDTO.getNome());
        conta.setEmail(contaDTO.getEmail());
        conta.setSenha(passwordEncoder.encode(contaDTO.getSenha()));

        //tipo da conta mudou
        TipoConta tipoAnterior = conta.getTipoConta();
        TipoConta tipoNovo = contaDTO.getTipoConta();
        conta.setTipoConta(tipoNovo);

        Conta atualizada = contaRepository.save(conta);

        if (tipoAnterior != tipoNovo) {
            //excluir entidade antiga, se existir
            if (tipoAnterior == TipoConta.USUARIO) usuarioRepository.findByConta(conta).ifPresent(usuarioRepository::delete);
            if (tipoAnterior == TipoConta.ARTISTA) artistaRepository.findByContaId(conta.getIdConta()).ifPresent(artistaRepository::delete);

            //criar nova entidade se necessário
            if (tipoNovo == TipoConta.USUARIO) {
                Usuario usuario = Usuario.builder()
                        .conta(atualizada)
                        .interesses(new ArrayList<>())
                        .build();
                usuarioRepository.save(usuario);
            } else if (tipoNovo == TipoConta.ARTISTA) {
                Artista artista = Artista.builder()
                        .conta(atualizada)
                        .build();
                artistaRepository.save(artista);
            }
        }

        return toResponseDTO(atualizada);
    }

    @Override
    @Transactional
    public boolean excluirConta(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada."));

        if (conta.getTipoConta() == TipoConta.USUARIO) {
            usuarioRepository.findByConta(conta).ifPresent(usuarioRepository::delete);
        } else if (conta.getTipoConta() == TipoConta.ARTISTA) {
            artistaRepository.findByContaId(conta.getIdConta()).ifPresent(artistaRepository::delete);
        }

        contaRepository.delete(conta);
        return true;
    }

    @Override
    public ContaResponseDTO buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada."));
        return toResponseDTO(conta);
    }

    private ContaResponseDTO toResponseDTO(Conta conta) {
        return ContaResponseDTO.builder()
                .idConta(conta.getIdConta())
                .nome(conta.getNome())
                .email(conta.getEmail())
                .tipoConta(conta.getTipoConta())
                .build();
    }
}