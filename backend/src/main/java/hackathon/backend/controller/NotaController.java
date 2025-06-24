package hackathon.backend.controller;

import hackathon.backend.service.NotaService;
import hackathon.backend.service.UsuarioService;
import hackathon.backend.model.Usuario;
import hackathon.backend.model.Perfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notas")
public class NotaController {

    @Autowired
    private NotaService notaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String consultarNotas(Model model) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        if (usuarioLogado.getPerfil() != Perfil.PROFESSOR) {
            return "redirect:/acesso-negado";
        }

        var notas = notaService.listarNotasPorProfessor(usuarioLogado.getId());

        model.addAttribute("notas", notas);
        return "nota/lista";
    }
}