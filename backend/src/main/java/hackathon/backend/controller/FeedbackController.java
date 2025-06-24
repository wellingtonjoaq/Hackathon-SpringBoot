package hackathon.backend.controller;

import hackathon.backend.model.Perfil;
import hackathon.backend.model.Usuario;
import hackathon.backend.service.FeedbackService;
import hackathon.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aluno/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String verFeedbacksAluno(Model model) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        if (usuarioLogado == null || usuarioLogado.getPerfil() != Perfil.ALUNO) {
            return "redirect:/acesso-negado";
        }

        model.addAttribute("feedbacks", feedbackService.listarFeedbacksDoAluno(usuarioLogado.getId()));
        return "aluno/feedbacks";
    }
}