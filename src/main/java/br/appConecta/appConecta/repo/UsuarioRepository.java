package br.appConecta.appConecta.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.appConecta.appConecta.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
	// SELECT * FROM usuario WHERE email = x 
	Optional<Usuario> findByEmail(String email);
}
