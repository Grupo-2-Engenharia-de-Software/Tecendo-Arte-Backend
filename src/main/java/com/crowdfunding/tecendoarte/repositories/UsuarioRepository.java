package com.crowdfunding.tecendoarte.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crowdfunding.tecendoarte.models.Conta;
import com.crowdfunding.tecendoarte.models.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>  {
    Optional<Usuario> findByConta(Conta conta);
    @Query("SELECT u FROM Usuario u WHERE u.conta.idConta = :contaId")
    Optional<Usuario> findByContaId(@Param("contaId") Long contaId);
}
