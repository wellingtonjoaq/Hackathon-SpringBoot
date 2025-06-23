package hackathon.backend.controller;

import hackathon.backend.model.*;
import hackathon.backend.repository.AlunoRepository;
import hackathon.backend.repository.TurmaRepository;
import hackathon.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping()
    public String iniciar(Usuario usuario, Model model) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("turmas", turmaRepository.findAll());
        return "usuario/formulario";
    }

    @PostMapping("salvar")
    public String salvar(Usuario usuario,
                         @RequestParam(value = "turmaId", required = false) Long turmaId,
                         Model model) {
        try {
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            } else if (usuario.getId() != null) {
                Usuario usuarioExistente = usuarioRepository.findById(usuario.getId()).orElse(null);
                if (usuarioExistente != null) {
                    usuario.setSenha(usuarioExistente.getSenha());
                }
            }

            usuarioRepository.save(usuario);

            if (usuario.getPerfil() == Perfil.ALUNO) {
                Turma turma = turmaRepository.findById(turmaId).orElse(null);
                if (turma != null) {
                    Aluno alunoExistente = alunoRepository.findByUsuario(usuario);
                    if (alunoExistente == null) {
                        Aluno aluno = new Aluno();
                        aluno.setUsuario(usuario);
                        aluno.setTurma(turma);
                        alunoRepository.save(aluno);
                    } else {
                        alunoExistente.setTurma(turma);
                        alunoRepository.save(alunoExistente);
                    }
                }
            } else {
                Aluno aluno = alunoRepository.findByUsuario(usuario);
                if (aluno != null) {
                    alunoRepository.delete(aluno);
                }
            }

            return "redirect:/usuario/listar";

        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar usuário");
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("perfis", Perfil.values());
            model.addAttribute("turmas", turmaRepository.findAll());
            return "usuario/formulario";
        }
    }

    @GetMapping("listar")
    public String listar(
            @RequestParam(required = false) Perfil perfil,
            @RequestParam(required = false) Long turmaId,
            Model model) {

        List<Usuario> usuarios = usuarioRepository.findAll();

        // Filtra por perfil, se informado
        if (perfil != null) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getPerfil() == perfil)
                    .toList();
        }

        // Filtra por turma, se informado
        if (turmaId != null) {
            usuarios = usuarios.stream()
                    .filter(u -> {
                        if (u.getPerfil() != Perfil.ALUNO) return false;
                        Aluno aluno = alunoRepository.findByUsuario(u);
                        return aluno != null && aluno.getTurma() != null && aluno.getTurma().getId().equals(turmaId);
                    })
                    .toList();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("turmas", turmaRepository.findAll());

        // Mantém os filtros selecionados na página
        model.addAttribute("filtroPerfil", perfil);
        model.addAttribute("filtroTurmaId", turmaId);

        return "usuario/lista";
    }

    @GetMapping("editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/usuario/listar";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        model.addAttribute("turmas", turmaRepository.findAll());
        return "usuario/formulario";
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuario/listar";
    }
}
