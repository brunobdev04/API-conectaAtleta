package br.appConecta.appConecta.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.appConecta.appConecta.model.Usuario;
import br.appConecta.appConecta.repo.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")

public class UsuarioController {

	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@PostMapping("/cadastrar")
	public Usuario cadastrarUsuario(@RequestBody Usuario novoUsuario) {
		
		Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
		return usuarioSalvo;
	}
	
	@GetMapping
    public List<Usuario> getTodosUsuarios() {
        return usuarioRepository.findAll(); 
    }
	
	@PostMapping("/login")
	public ResponseEntity<Usuario> fazerLogin(@RequestBody LoginRequest loginRequest) {
		Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(loginRequest.email());
		
		//validaUser
		if (usuarioOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		Usuario usuario = usuarioOptional.get();
		
		//validaSenha
		if (usuario.getSenha().equals(loginRequest.senha())) {
			return ResponseEntity.ok(usuario);
			//Essa proteção de senha não usa biblioteca portanto é sem segurança real.
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	
	// metodo pra puxar id (já criado no JPA)
	@GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isPresent()) {
    
            return ResponseEntity.ok(usuario.get());
        } else {

            return ResponseEntity.notFound().build();
        }
    }
	
	// UPDATE (nome, email, senha)
	
	@PatchMapping("/{id}/nome")
	public ResponseEntity<Usuario> atualizaNome(
			@PathVariable long id, 
			@RequestBody UpdateNome request) {
		
		return usuarioRepository.findById(id).map(usuario -> {
			usuario.setNome(request.nome());
			return ResponseEntity.ok(usuarioRepository.save(usuario));
		}) .orElse(ResponseEntity.notFound().build());
	}
	
	@PatchMapping("/{id}/email")
	public ResponseEntity<Usuario> atualizaEmail(
			@PathVariable long id,
			@RequestBody UpdateEmail request) {
		
		return usuarioRepository.findById(id).map(usuario -> {
			usuario.setEmail(request.email());
			return ResponseEntity.ok(usuarioRepository.save(usuario));
		}) .orElse(ResponseEntity.notFound().build());
	}
	
	@PatchMapping("/{id}/senha")
	public ResponseEntity<Usuario> atualizaSenha(
			@PathVariable long id,
			@RequestBody UpdateSenha request) {
		
		Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
		
		if (usuarioOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		//if senhaAntiga !=
		Usuario usuario = usuarioOptional.get();
		
		if (!usuario.getSenha().equals(request.senhaAntiga())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		usuario.setSenha(request.senhaNova());
		return ResponseEntity.ok(usuarioRepository.save(usuario));
	}
	
	
	// Ataca o id checando se existe, se ele existe manda excluir se não existe
	@DeleteMapping("/{id}")
	
	public ResponseEntity<Void> deletaUsuario(@PathVariable Long id) {
		
		if (!usuarioRepository.existsById(id)) {
			
			return ResponseEntity.notFound().build();
		}
		
		usuarioRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
}

