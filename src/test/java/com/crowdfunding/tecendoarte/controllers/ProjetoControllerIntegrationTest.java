package com.crowdfunding.tecendoarte.controllers;

import com.crowdfunding.tecendoarte.models.Artista;
import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Projeto;
import com.crowdfunding.tecendoarte.models.enums.TipoArte;
import com.crowdfunding.tecendoarte.models.enums.TipoConta;
import com.crowdfunding.tecendoarte.models.enums.StatusProjeto;
import com.crowdfunding.tecendoarte.repositories.ArtistaRepository;
import com.crowdfunding.tecendoarte.repositories.ProjetoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProjetoControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Artista artista;

    @BeforeEach
    void setUp() {
        projetoRepository.deleteAll();
        artistaRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Conta conta = Conta.builder()
                .nome("Artista Teste")
                .email("artista@exemplo.com")
                .senha(passwordEncoder.encode("senha123"))
                .tipoConta(TipoConta.ARTISTA)
                .build();

        artista = Artista.builder()
                .conta(conta)
                .descricao("Artista de pintura")
                .categorias(List.of(TipoArte.PINTURA))
                .build();
        artista = artistaRepository.save(artista);
    }

    private String buildProjetoRequestJson(String titulo, String descricao, double meta, String tipoArte) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "titulo", titulo,
                "descricaoProjeto", descricao,
                "meta", meta,
                "tipoArte", tipoArte
        ));
    }

    @Test
    void deveCadastrarProjetoComSucesso() throws Exception {
        String payload = buildProjetoRequestJson("Projeto 1", "Descrição do projeto", 1000.0, "PINTURA");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(artista.getConta().getEmail(), null, List.of())
        );

        mockMvc.perform(post("/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Projeto 1"))
                .andExpect(jsonPath("$.descricaoProjeto").value("Descrição do projeto"))
                .andExpect(jsonPath("$.meta").value(1000.0))
                .andExpect(jsonPath("$.tipoArte").value("PINTURA"));
    }

    @Test
    void deveBuscarProjetoPorIdComSucesso() throws Exception {
        Projeto projeto = Projeto.builder()
                .titulo("Projeto 2")
                .descricaoProjeto("Desc")
                .meta(500.0)
                .tipoArte(TipoArte.PINTURA)
                .artista(artista)
                .dataCriacao(LocalDate.now())
                .status(StatusProjeto.AGUARDANDO_AVALIACAO)
                .valorArrecadado(0.0)
                .build();
        projeto = projetoRepository.save(projeto);

        mockMvc.perform(get("/projetos/" + projeto.getIdProjeto()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProjeto").value(projeto.getIdProjeto()))
                .andExpect(jsonPath("$.titulo").value("Projeto 2"));
    }

    @Test
    void deveRetornarNotFoundAoBuscarProjetoInexistente() throws Exception {
        mockMvc.perform(get("/projetos/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarProjetoComSucesso() throws Exception {
        Projeto projeto = Projeto.builder()
                .titulo("Projeto Antigo")
                .descricaoProjeto("Desc")
                .meta(500.0)
                .tipoArte(TipoArte.PINTURA)
                .artista(artista)
                .dataCriacao(LocalDate.now())
                .status(StatusProjeto.AGUARDANDO_AVALIACAO)
                .valorArrecadado(0.0)
                .build();
        projeto = projetoRepository.save(projeto);

        String payload = buildProjetoRequestJson("Projeto Atualizado", "Nova descrição", 1500.0, "PINTURA");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(artista.getConta().getEmail(), null, List.of())
        );

        mockMvc.perform(put("/projetos/" + projeto.getIdProjeto())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Projeto Atualizado"))
                .andExpect(jsonPath("$.descricaoProjeto").value("Nova descrição"))
                .andExpect(jsonPath("$.meta").value(1500.0));
    }

    @Test
    void deveDeletarProjetoComSucesso() throws Exception {
        Projeto projeto = Projeto.builder()
                .titulo("Projeto Deletar")
                .descricaoProjeto("Desc")
                .meta(500.0)
                .tipoArte(TipoArte.PINTURA)
                .artista(artista)
                .dataCriacao(LocalDate.now())
                .status(StatusProjeto.AGUARDANDO_AVALIACAO)
                .valorArrecadado(0.0)
                .build();
        projeto = projetoRepository.save(projeto);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(artista.getConta().getEmail(), null, List.of())
        );

        mockMvc.perform(delete("/projetos/" + projeto.getIdProjeto()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Projeto deletado com sucesso."))
                .andExpect(jsonPath("$.idProjeto").value(projeto.getIdProjeto()));
    }

    @Test
    void deveConsultarProjetosPorArtista() throws Exception {
        Projeto projeto1 = Projeto.builder()
                .titulo("Projeto A")
                .descricaoProjeto("Desc A")
                .meta(100.0)
                .tipoArte(TipoArte.PINTURA)
                .artista(artista)
                .dataCriacao(LocalDate.now())
                .status(StatusProjeto.AGUARDANDO_AVALIACAO)
                .valorArrecadado(0.0)
                .build();
        Projeto projeto2 = Projeto.builder()
                .titulo("Projeto B")
                .descricaoProjeto("Desc B")
                .meta(200.0)
                .tipoArte(TipoArte.ESCULTURA)
                .artista(artista)
                .dataCriacao(LocalDate.now())
                .status(StatusProjeto.AGUARDANDO_AVALIACAO)
                .valorArrecadado(0.0)
                .build();
        projetoRepository.saveAll(List.of(projeto1, projeto2));

        mockMvc.perform(get("/projetos/consultar-por-artista")
                        .param("nomeArtista", artista.getConta().getNome()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Projeto A"))
                .andExpect(jsonPath("$[1].titulo").value("Projeto B"));
    }

    @Test
    void deveRetornarNotFoundAoConsultarProjetosPorArtistaInexistente() throws Exception {
        mockMvc.perform(get("/projetos/consultar-por-artista")
                        .param("nomeArtista", "Inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Artista não encontrado"));
    }
}