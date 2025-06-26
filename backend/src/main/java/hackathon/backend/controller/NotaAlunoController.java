package hackathon.backend.controller;

import hackathon.backend.model.Perfil;
import hackathon.backend.model.Usuario;
import hackathon.backend.service.NotaAlunoService;
import hackathon.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aluno/notas")
public class NotaAlunoController {

    @Autowired
    private NotaAlunoService notaAlunoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String verNotasAluno(Model model) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        if (usuarioLogado == null || usuarioLogado.getPerfil() != Perfil.ALUNO) {
            return "redirect:/acesso-negado";
        }

        model.addAttribute("notas", notaAlunoService.listarNotasDoAluno(usuarioLogado.getId()));
        model.addAttribute("mediaNotas", notaAlunoService.calcularMediaNotasDoAluno(usuarioLogado.getId()));
        return "aluno/notas";
    }
}