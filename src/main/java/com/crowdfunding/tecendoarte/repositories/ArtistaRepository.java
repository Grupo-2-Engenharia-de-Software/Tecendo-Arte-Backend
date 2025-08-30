package com.crowdfunding.tecendoarte.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.crowdfunding.tecendoarte.models.Artista;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    
    Optional<Artista> findByContaEmail(String email);
    Optional<Artista> findByContaNomeContainingIgnoreCase(String nome);
    @Query("SELECT u FROM Usuario u WHERE u.conta.idConta = :contaId")
    Optional<Artista> findByContaId(@Param("contaId") Long contaId);
}