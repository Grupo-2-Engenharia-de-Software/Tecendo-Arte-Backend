package com.crowdfunding.tecendoarte.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crowdfunding.tecendoarte.models.Artista;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    
    Optional<Artista> findByContaEmail(String email);
    Optional<Artista> findByNomeContainingIgnoreCase(String nome);

}