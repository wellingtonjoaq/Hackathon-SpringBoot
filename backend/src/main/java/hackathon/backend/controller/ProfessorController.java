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
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/painel")
    public String painelProfessor(Model model) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        if (usuarioLogado == null || usuarioLogado.getPerfil() != Perfil.PROFESSOR) {
            return "redirect:/acesso-negado";
        }

        model.addAttribute("usuario", usuarioLogado);
        return "professor/painel";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "error/acesso-negado";
    }
}