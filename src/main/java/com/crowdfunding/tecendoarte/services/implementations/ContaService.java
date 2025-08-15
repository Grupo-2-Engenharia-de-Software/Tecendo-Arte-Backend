package com.crowdfunding.tecendoarte.services.implementations;

import com.crowdfunding.tecendoarte.dto.ContaDTO.*;
import com.crowdfunding.tecendoarte.models.enums.*;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.services.interfaces.ContaServiceInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaService implements ContaServiceInterface {
    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    public ContaService(ContaRepository conta, PasswordEncoder password){
        this.contaRepository = conta;
        this.passwordEncoder = password;
    }

    @Override
    public ContaResponseDTO cadastrar(ContaRequestDTO contaDTO) {
        if (!contaDTO.getSenha().equals(contaDTO.getConfirmacaoSenha())) {
            throw new IllegalArgumentException("As senhas nao coincidem.");
        }

        contaRepository.findByEmail(contaDTO.getEmail()).ifPresent(conta -> {
            throw new IllegalArgumentException("Ja existe uma conta com este e-mail.");
        });

        Conta conta = Conta.builder()
            .email(contaDTO.getEmail())
            .senha(passwordEncoder.encode(contaDTO.getSenha()))
            .nome(contaDTO.getNome())
            .tipoConta(contaDTO.getTipoConta())
            .build();

        Conta contaSalva = contaRepository.save(conta);

        return ContaResponseDTO.builder()
                .idConta(contaSalva.getIdConta())
                .email(contaSalva.getEmail())
                .nome(contaSalva.getNome())
                .tipoConta(contaSalva.getTipoConta())
                .build();
    }

    @Override
    @Transactional
    public ContaResponseDTO atualizarConta(Long id, ContaRequestDTO contaDTO) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

        if (!contaDTO.getSenha().equals(contaDTO.getConfirmacaoSenha())) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        conta.setNome(contaDTO.getNome());
        conta.setEmail(contaDTO.getEmail());
        conta.setSenha(passwordEncoder.encode(contaDTO.getSenha()));
        conta.setTipoConta(contaDTO.getTipoConta());

        Conta atualizada = contaRepository.save(conta);
        return toResponseDTO(atualizada);
    }


    @Override
    @Transactional
    public boolean excluirConta(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada."));
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