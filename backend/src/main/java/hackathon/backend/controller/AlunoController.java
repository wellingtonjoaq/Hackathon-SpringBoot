package hackathon.backend.controller;

import hackathon.backend.model.Perfil;
import hackathon.backend.model.Usuario;
import hackathon.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/painel")
    public String painelAluno(Model model) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        if (usuarioLogado == null || usuarioLogado.getPerfil() != Perfil.ALUNO) {
            return "redirect:/acesso-negado";
        }

        model.addAttribute("usuario", usuarioLogado);
        return "aluno/painel";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "error/acesso-negado";
    }
}