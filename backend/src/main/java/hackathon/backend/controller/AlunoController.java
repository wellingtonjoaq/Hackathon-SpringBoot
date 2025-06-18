package hackathon.backend.controller;

import hackathon.backend.dto.AlunoDTO;
import hackathon.backend.model.Perfil;
import hackathon.backend.service.AlunoService;
import hackathon.backend.service.TurmaService;
import hackathon.backend.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("aluno")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping()
    public String iniciar(AlunoDTO alunoDTO, Model model) {
        var alunosUsuarios = usuarioService.listarTodos()
                .stream()
                .filter(u -> u.getPerfil() == Perfil.ALUNO)
                .collect(Collectors.toList());
        model.addAttribute("usuarios", alunosUsuarios);
        model.addAttribute("turmas", turmaService.listarTodos());
        model.addAttribute("alunoDTO", alunoDTO);
        return "aluno/formulario";
    }

    @PostMapping("salvar")
    public String salvar(@ModelAttribute AlunoDTO alunoDTO, Model model) {
        try {
            alunoService.salvar(modelMapper.map(alunoDTO, hackathon.backend.model.Aluno.class));
            return "redirect:/aluno/listar";
        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar aluno");
            model.addAttribute("erro", e.getMessage());
            return iniciar(alunoDTO, model);
        }
    }

    @GetMapping("listar")
    public String listar(Model model) {
        var alunos = alunoService.listarTodos()
                .stream()
                .map(a -> modelMapper.map(a, AlunoDTO.class))
                .collect(Collectors.toList());
        model.addAttribute("alunos", alunos);
        return "aluno/lista";
    }

    @GetMapping("editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var aluno = alunoService.buscarPorId(id);
        model.addAttribute("alunoDTO", modelMapper.map(aluno, AlunoDTO.class));
        return iniciar(modelMapper.map(aluno, AlunoDTO.class), model);
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        alunoService.deletarPorId(id);
        return "redirect:/aluno/listar";
    }
}
