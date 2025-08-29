package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.dto.ContaDTO.ContaRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.models.Usuario;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.crowdfunding.tecendoarte.repositories.ContaRepository;
import com.crowdfunding.tecendoarte.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) 
class UsuarioControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ContaRepository contaRepository;

    // ---------- Helpers ----------
    private String novoEmail() {
        return "conta_" + UUID.randomUUID() + "@email.com";
    }

    private Long criarConta() throws Exception {
        ContaRequestDTO contaDto = ContaRequestDTO.builder()
                .nome("Conta Teste")
                .email(novoEmail())
                .senha("123456")
                .tipoConta(TipoConta.USUARIO)
                .build();

        var resp = mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idConta").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(resp).get("idConta").asLong();
    }

    // ---------- Testes ----------

    @Test
    void criarUsuario_comContaValida_deveRetornarCriado() throws Exception {
        Long contaId = criarConta();

        Usuario usuario = usuarioRepository.findByContaId(contaId)
                .orElseThrow(() -> new AssertionError("Usuario não foi criado automaticamente"));

        assertNotNull(usuario.getId());
        assertEquals(contaId, usuario.getConta().getIdConta());
        assertTrue(usuario.getInteresses().isEmpty());
    }

    @Test
    void buscarUsuario_existente_deveRetornarOk() throws Exception {
        Long contaId = criarConta();
        Usuario usuario = usuarioRepository.findByConta(contaRepository.findById(contaId).get()).get();
        Long usuarioId = usuario.getId();

        mockMvc.perform(get("/usuarios/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId))
                .andExpect(jsonPath("$.contaId").value(contaId));
    }

    @Test
    void atualizarUsuario_mudarInteresses_deveRetornarOk() throws Exception {
        Long contaId = criarConta();
        Usuario usuario = usuarioRepository.findByConta(contaRepository.findById(contaId).get()).get();
        Long usuarioId = usuario.getId();

        UsuarioRequestDTO req = new UsuarioRequestDTO();
        req.setContaId(contaId);
        req.setInteresses(List.of(TipoArte.FOTOGRAFIA));

        mockMvc.perform(put("/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId))
                .andExpect(jsonPath("$.contaId").value(contaId))
                .andExpect(jsonPath("$.interesses[0]").value("FOTOGRAFIA"));
    }

    @Test
    void deletarUsuario_existente_deveRetornarNoContent() throws Exception {
        Long contaId = criarConta();
        Usuario usuario = usuarioRepository.findByContaId(contaId)
                .orElseThrow(() -> new AssertionError("Usuario não foi criado automaticamente"));
        Long usuarioId = usuario.getId();

        mockMvc.perform(delete("/usuarios/{id}", usuarioId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/usuarios/{id}", usuarioId))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarUsuario_semConta_deveRetornarBadRequest() throws Exception {
        UsuarioRequestDTO req = new UsuarioRequestDTO();

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("contaId")));
    }

    @Test
    void buscarUsuario_inexistente_deveRetornarNotFound() throws Exception {
        mockMvc.perform(get("/usuarios/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarUsuario_inexistente_deveRetornarNotFound() throws Exception {
        mockMvc.perform(delete("/usuarios/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void naoDeveCriarUsuarioSeContaJaPossuiUsuario() throws Exception {
        Long contaId = criarConta();

        UsuarioRequestDTO segundo = new UsuarioRequestDTO();
        segundo.setContaId(contaId);
        segundo.setInteresses(List.of(TipoArte.ESCULTURA));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundo)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Ja existe um usuario vinculado a essa conta.")));
    }

    @Test
    void criarUsuario_comContaInexistente_deveRetornarNotFound() throws Exception {
        UsuarioRequestDTO req = new UsuarioRequestDTO();
        req.setContaId(999999L);
        req.setInteresses(List.of(TipoArte.PINTURA));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }
}
