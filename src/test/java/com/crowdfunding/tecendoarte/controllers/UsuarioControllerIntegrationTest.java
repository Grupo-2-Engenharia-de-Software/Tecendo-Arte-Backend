package com.crowdfunding.tecendoarte.controllers;


import com.crowdfunding.tecendoarte.dto.ContaDTO.ContaRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioRequestDTO;
import com.crowdfunding.tecendoarte.dto.UsuarioDTO.UsuarioResponseDTO;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long usuarioIdCriado;

    @BeforeEach
    void setUp() throws Exception {
        Long contaId = criarContaEObterId();
        usuarioIdCriado = criarUsuario(contaId, null).getId();
    }

    private Long criarContaEObterId() throws Exception {
        ContaRequestDTO contaDto = new ContaRequestDTO();
        contaDto.setNome("Teste");
        contaDto.setEmail("teste" + System.currentTimeMillis() + "@example.com");
        contaDto.setSenha("123456");
        contaDto.setTipoConta(TipoConta.USUARIO);

        String responseConta = mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contaDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idConta").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseConta).get("idConta").asLong();
    }

    private UsuarioResponseDTO criarUsuario(Long contaId, List<TipoArte> interesses) throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setContaId(contaId);
        dto.setInteresses(interesses != null ? interesses : List.of(TipoArte.DESENHO));

        String responseUsuario = mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(responseUsuario, UsuarioResponseDTO.class);
    }

    @Test
    void criarUsuario_sucesso() throws Exception {
        Long contaId = criarContaEObterId(); // <<< cria a conta primeiro

        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setContaId(contaId); // usa o ID real da conta criada
        dto.setInteresses(List.of(TipoArte.DESENHO, TipoArte.PINTURA));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.contaId").value(contaId))
                .andExpect(jsonPath("$.interesses[0]").value("DESENHO"));
    }

    @Test
    void buscarUsuario_porId_eValidarCampos() throws Exception {
        Long contaId = criarContaEObterId();

        // cria o usuário depois da conta
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setContaId(contaId);
        dto.setInteresses(List.of(TipoArte.DESENHO));

        String usuarioResp = mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long usuarioId = objectMapper.readTree(usuarioResp).get("id").asLong();

        // busca o usuário criado
        String resp = mockMvc.perform(get("/usuarios/{id}", usuarioId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UsuarioResponseDTO buscado = objectMapper.readValue(resp, UsuarioResponseDTO.class);

        assertThat(buscado.getId()).isEqualTo(usuarioId);
        assertThat(buscado.getContaId()).isEqualTo(contaId);
        assertThat(buscado.getInteresses()).isNotEmpty();
    }

    @Test
    void buscarUsuario_porId() throws Exception {
        mockMvc.perform(get("/usuarios/{id}", usuarioIdCriado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioIdCriado));
    }

    @Test
    void atualizarUsuario_interesses() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setContaId(1L);
        dto.setInteresses(List.of(TipoArte.ESCULTURA));

        mockMvc.perform(put("/usuarios/{id}", usuarioIdCriado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interesses[0]").value("ESCULTURA"));
    }

    @Test
    void deletarUsuario() throws Exception {
        mockMvc.perform(delete("/usuarios/{id}", usuarioIdCriado))
                .andExpect(status().isNoContent());

        // Garantir que não existe mais
        mockMvc.perform(get("/usuarios/{id}", usuarioIdCriado))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarUsuario_semConta_deveFalharValidacao() throws Exception {
        UsuarioRequestDTO req = new UsuarioRequestDTO();
        // contaId não setado
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.contaId").exists());
    }

    @Test
    void deveRetornarErroAoBuscarUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/usuarios/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornarErroAoDeletarUsuarioInexistente() throws Exception {
        mockMvc.perform(delete("/usuarios/{id}", 9999))
                .andExpect(status().isNotFound());
    }

}