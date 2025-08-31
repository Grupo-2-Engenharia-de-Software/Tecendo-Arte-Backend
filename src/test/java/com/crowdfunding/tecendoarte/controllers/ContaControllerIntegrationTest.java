package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.dto.ContaDTO.ContaRequestDTO;
import com.crowdfunding.tecendoarte.dto.ContaDTO.ContaResponseDTO;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ContaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long contaIdCriada;

    @BeforeEach
    void setUp() throws Exception {
        contaIdCriada = criarContaEObterId("Teste", "teste" + System.currentTimeMillis() + "@example.com");
    }

    private Long criarContaEObterId(String nome, String email) throws Exception {
        ContaRequestDTO contaDTO = new ContaRequestDTO();
        contaDTO.setNome(nome);
        contaDTO.setEmail(email);
        contaDTO.setSenha("123456");
        contaDTO.setTipoConta(TipoConta.USUARIO);

        String response = mockMvc.perform(post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contaDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("idConta").asLong();
    }

    @Test
    @Transactional
    void criarConta_sucesso() throws Exception {
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Maria");
        dto.setEmail("maria" + System.currentTimeMillis() + "@example.com");
        dto.setSenha("abcdef");
        dto.setTipoConta(TipoConta.USUARIO);

        mockMvc.perform(post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idConta").exists())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.tipoConta").value("USUARIO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER" })
    void buscarConta_porId() throws Exception {
        String response = mockMvc.perform(get("/contas/{id}", contaIdCriada))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ContaResponseDTO buscada = objectMapper.readValue(response, ContaResponseDTO.class);
        assertThat(buscada.getIdConta()).isEqualTo(contaIdCriada);
        assertThat(buscada.getNome()).isNotBlank();
        assertThat(buscada.getEmail()).isNotBlank();
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER" })
    void atualizarConta_sucesso() throws Exception {
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("NomeAtualizado");
        dto.setEmail("atualizado" + System.currentTimeMillis() + "@example.com");
        dto.setSenha("novaSenha");
        dto.setTipoConta(TipoConta.USUARIO);

        String response = mockMvc.perform(put("/contas/{id}", contaIdCriada)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ContaResponseDTO atualizada = objectMapper.readValue(response, ContaResponseDTO.class);
        assertThat(atualizada.getNome()).isEqualTo("NomeAtualizado");
        assertThat(atualizada.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER" })
    void deletarConta_sucesso() throws Exception {
        mockMvc.perform(delete("/contas/{id}", contaIdCriada))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/contas/{id}", contaIdCriada))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void criarConta_duplicada_deveFalhar() throws Exception {
        String emailDuplicado = "testeDuplicado" + System.currentTimeMillis() + "@example.com";

        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("TesteDuplicado");
        dto.setEmail(emailDuplicado);
        dto.setSenha("123456");
        dto.setTipoConta(TipoConta.USUARIO);

        // 1ª vez → deve criar
        mockMvc.perform(post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // 2ª vez (mesmo e-mail) → deve falhar
        mockMvc.perform(post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("email")));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER" })
    void buscarConta_inexistente_deveRetornar404() throws Exception {
        mockMvc.perform(get("/contas/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void criarConta_dtoInvalido_deveRetornar400() throws Exception {
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("Teste Invalido");
        dto.setEmail("");
        dto.setSenha("123456");
        dto.setTipoConta(TipoConta.USUARIO);

        String response = mockMvc.perform(post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
                
        assertThat(response).contains("O email é obrigatório");
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER" })
    void atualizarConta_inexistente_deveRetornar404() throws Exception {
        ContaRequestDTO dto = new ContaRequestDTO();
        dto.setNome("NomeAtualizado");
        dto.setEmail("atualizado" + System.currentTimeMillis() + "@example.com");
        dto.setSenha("novaSenha");
        dto.setTipoConta(TipoConta.USUARIO);

        mockMvc.perform(put("/contas/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "USER" })
    void deletarConta_inexistente_deveRetornar404() throws Exception {
        mockMvc.perform(delete("/contas/{id}", 99999L))
                .andExpect(status().isNotFound());
    }
}
