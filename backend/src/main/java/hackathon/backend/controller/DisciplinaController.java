package hackathon.backend.controller;

import hackathon.backend.dto.DisciplinaDTO;
import hackathon.backend.model.Perfil;
import hackathon.backend.service.DisciplinaService;
import hackathon.backend.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("disciplina")
public class DisciplinaController {

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping()
    public String iniciar(DisciplinaDTO disciplinaDTO, Model model) {
        var professores = usuarioService.listarTodos()
                .stream()
                .filter(u -> u.getPerfil() == Perfil.PROFESSOR)
                .collect(Collectors.toList());
        model.addAttribute("professores", professores);
        model.addAttribute("disciplinaDTO", disciplinaDTO);
        return "disciplina/formulario";
    }

    @PostMapping("salvar")
    public String salvar(@ModelAttribute DisciplinaDTO disciplinaDTO, Model model) {
        try {
            disciplinaService.salvar(modelMapper.map(disciplinaDTO, hackathon.backend.model.Disciplina.class));
            return "redirect:/disciplina/listar";
        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar disciplina");
            model.addAttribute("erro", e.getMessage());
            return iniciar(disciplinaDTO, model);
        }
    }

    // Método listar modificado para aceitar filtros
    @GetMapping("listar")
    public String listar(
            @RequestParam(name = "filtroNome", required = false) String filtroNome,
            @RequestParam(name = "filtroProfessor", required = false) String filtroProfessor,
            Model model) {

        var disciplinas = disciplinaService.buscarComFiltros(filtroNome, filtroProfessor)
                .stream()
                .map(disc -> {
                    DisciplinaDTO dto = modelMapper.map(disc, DisciplinaDTO.class);
                    if (disc.getProfessor() != null) {
                        dto.setNomeProfessor(disc.getProfessor().getNome());
                    } else {
                        dto.setNomeProfessor("Sem Professor");
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        var professores = disciplinaService.listarProfessoresUnicos();

        model.addAttribute("disciplinas", disciplinas);
        model.addAttribute("professores", professores);

        // Valores atuais dos filtros para manter no formulário
        model.addAttribute("filtroNome", filtroNome);
        model.addAttribute("filtroProfessor", filtroProfessor);

        return "disciplina/lista";
    }

    @GetMapping("editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var disciplina = disciplinaService.buscarPorId(id);
        DisciplinaDTO dto = modelMapper.map(disciplina, DisciplinaDTO.class);

        if (disciplina.getProfessor() != null) {
            dto.setProfessorId(disciplina.getProfessor().getId());
        }

        model.addAttribute("disciplinaDTO", dto);
        return iniciar(dto, model);
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        disciplinaService.deletarPorId(id);
        return "redirect:/disciplina/listar";
    }
}
