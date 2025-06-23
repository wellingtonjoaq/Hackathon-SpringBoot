package hackathon.backend.service;

import hackathon.backend.model.Aluno;
import hackathon.backend.model.Perfil;
import hackathon.backend.model.Turma;
import hackathon.backend.model.Usuario;
import hackathon.backend.repository.AlunoRepository;
import hackathon.backend.repository.UsuarioRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }



    public List<Usuario> listarComFiltro(Perfil perfil, Turma turma) {
        List<Usuario> usuarios = repository.findAll();

        if (perfil != null) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getPerfil() == perfil)
                    .collect(Collectors.toList());
        }

        if (turma != null) {
            usuarios = usuarios.stream()
                    .filter(u -> {
                        if (u.getPerfil() != Perfil.ALUNO) return false;
                        Aluno aluno = alunoRepository.findByUsuario(u);
                        return aluno != null && aluno.getTurma() != null && aluno.getTurma().equals(turma);
                    })
                    .collect(Collectors.toList());
        }

        return usuarios;
    }

    public Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }
}
