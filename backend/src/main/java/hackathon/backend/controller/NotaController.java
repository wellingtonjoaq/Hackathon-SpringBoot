package hackathon.backend.controller;

import hackathon.backend.model.Perfil;
import hackathon.backend.model.Usuario;
import hackathon.backend.service.NotaService;
import hackathon.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/notas")
public class NotaController {

    @Autowired
    private NotaService notaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String consultarNotas(
            @RequestParam(name = "filtroTurma", required = false) String filtroTurma,
            @RequestParam(name = "filtroDisciplina", required = false) String filtroDisciplina,
            @RequestParam(name = "dataProva", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataProva,
            Model model) {

        Usuario usuarioLogado = usuarioService.getUsuarioLogado();

        if (usuarioLogado.getPerfil() != Perfil.PROFESSOR) {
            return "redirect:/acesso-negado";
        }

        var notasFiltradas = notaService.listarNotasFiltradas(
                usuarioLogado.getId(), filtroTurma, filtroDisciplina, dataProva);

        var turmas = notaService.listarTurmasDoProfessor(usuarioLogado.getId());
        var disciplinas = notaService.listarDisciplinasDoProfessor(usuarioLogado.getId());

        model.addAttribute("notas", notasFiltradas);
        model.addAttribute("turmas", turmas);
        model.addAttribute("disciplinas", disciplinas);
        model.addAttribute("filtroTurma", filtroTurma);
        model.addAttribute("filtroDisciplina", filtroDisciplina);
        model.addAttribute("dataProva", dataProva);

        return "nota/lista";
    }
}