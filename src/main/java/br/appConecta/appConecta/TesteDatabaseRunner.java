package br.appConecta.appConecta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.appConecta.appConecta.model.TipoUsuario;
import br.appConecta.appConecta.model.Usuario;
import br.appConecta.appConecta.repo.UsuarioRepository;

//mas nao e pra rodar toda hora
//@Component
public class TesteDatabaseRunner implements CommandLineRunner {

	// mete um repository gostozinho
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
		System.out.println("--- 🚀 INICIANDO TESTE DE CADASTRO ---");
		
		// 1. Cria um usuário de teste
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Usuario de Teste");
        novoUsuario.setEmail("teste@email.com");
        novoUsuario.setSenha("123456");
        novoUsuario.setTipoUsuario(TipoUsuario.ATLETA); // Usa o Enum

        // 2. Salva o usuário no banco
        usuarioRepository.save(novoUsuario);

        System.out.println("--- ✅ USUÁRIO SALVO COM SUCESSO ---");
        System.out.println("--- ID do usuário: " + novoUsuario.getId());
	}
}
