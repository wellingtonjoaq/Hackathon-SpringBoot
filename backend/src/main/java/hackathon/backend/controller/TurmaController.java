package hackathon.backend.controller;

import hackathon.backend.dto.TurmaDTO;
import hackathon.backend.model.Turma;
import hackathon.backend.service.TurmaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("turma")
public class TurmaController {

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping()
    public String iniciar(TurmaDTO turmaDTO, Model model) {
        model.addAttribute("turmaDTO", turmaDTO);
        return "turma/formulario";
    }

    @PostMapping("salvar")
    public String salvar(@ModelAttribute TurmaDTO turmaDTO, Model model) {
        try {
            turmaService.salvar(modelMapper.map(turmaDTO, hackathon.backend.model.Turma.class));
            return "redirect:/turma/listar";
        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar turma");
            model.addAttribute("erro", e.getMessage());
            return iniciar(turmaDTO, model);
        }
    }


    @GetMapping("listar")
    public String listar(
            @RequestParam(name = "filtroNome", required = false) String filtroNome,
            @RequestParam(name = "filtroPeriodo", required = false) String filtroPeriodo,
            @RequestParam(name = "filtroCurso", required = false) String filtroCurso,
            Model model) {

        var turmas = turmaService.buscarComFiltros(filtroNome, filtroPeriodo, filtroCurso);

        var turmasDTO = turmas.stream()
                .map(t -> modelMapper.map(t, TurmaDTO.class))
                .collect(Collectors.toList());

        var periodos = turmaService.buscarPeriodos();
        var cursos = turmaService.buscarCursos();

        model.addAttribute("turmas", turmasDTO);
        model.addAttribute("filtroNome", filtroNome);
        model.addAttribute("filtroPeriodo", filtroPeriodo);
        model.addAttribute("filtroCurso", filtroCurso);
        model.addAttribute("periodos", periodos);
        model.addAttribute("cursos", cursos);

        return "turma/lista";
    }



    @GetMapping("editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var turma = turmaService.buscarPorId(id);
        model.addAttribute("turmaDTO", modelMapper.map(turma, TurmaDTO.class));
        return iniciar(modelMapper.map(turma, TurmaDTO.class), model);
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        turmaService.deletarPorId(id);
        return "redirect:/turma/listar";
    }
}
