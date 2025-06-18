package hackathon.backend.controller;

import hackathon.backend.dto.ProvaDTO;
import hackathon.backend.service.DisciplinaService;
import hackathon.backend.service.ProvaService;
import hackathon.backend.service.TurmaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("prova")
public class ProvaController {

    @Autowired
    private ProvaService provaService;

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping()
    public String iniciar(ProvaDTO provaDTO, Model model) {
        model.addAttribute("turmas", turmaService.listarTodos());
        model.addAttribute("disciplinas", disciplinaService.listarTodos());
        model.addAttribute("provaDTO", provaDTO);
        return "prova/formulario";
    }

    @PostMapping("salvar")
    public String salvar(@ModelAttribute ProvaDTO provaDTO, Model model) {
        try {
            provaService.salvar(modelMapper.map(provaDTO, hackathon.backend.model.Prova.class));
            return "redirect:/prova/listar";
        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar prova");
            model.addAttribute("erro", e.getMessage());
            return iniciar(provaDTO, model);
        }
    }

    @GetMapping("listar")
    public String listar(Model model) {
        var provas = provaService.listarTodos()
                .stream()
                .map(p -> modelMapper.map(p, ProvaDTO.class))
                .collect(Collectors.toList());
        model.addAttribute("provas", provas);
        return "prova/lista";
    }

    @GetMapping("editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var prova = provaService.buscarPorId(id);
        model.addAttribute("provaDTO", modelMapper.map(prova, ProvaDTO.class));
        return iniciar(modelMapper.map(prova, ProvaDTO.class), model);
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        provaService.deletarPorId(id);
        return "redirect:/prova/listar";
    }
}
