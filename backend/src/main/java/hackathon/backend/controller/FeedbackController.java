package hackathon.backend.controller;


import hackathon.backend.service.FeedbackService;
import hackathon.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/aluno")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/{alunoId}/feedbacks")
    @ResponseBody
    public List<hackathon.backend.dto.FeedbackDTO> getFeedbacksAluno(@PathVariable Long alunoId) {
        return feedbackService.listarFeedbacksDoAluno(alunoId);
    }
}