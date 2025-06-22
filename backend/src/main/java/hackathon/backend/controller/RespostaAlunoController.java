package hackathon.backend.controller;

import hackathon.backend.dto.RespostaAlunoDTO;
import hackathon.backend.model.Perfil;
import hackathon.backend.service.AlunoService;
import hackathon.backend.service.ProvaService;
import hackathon.backend.service.RespostaAlunoService;
import hackathon.backend.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
@RequestMapping("resposta")
public class RespostaAlunoController {

    @Autowired
    private RespostaAlunoService respostaAlunoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProvaService provaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping()
    public String iniciar(RespostaAlunoDTO respostaAlunoDTO, Model model) {
        if (respostaAlunoDTO.getDetalhes() == null) {
            respostaAlunoDTO.setDetalhes(new ArrayList<>());
        }
        var alunos = usuarioService.listarTodos()
                .stream()
                .filter(u -> u.getPerfil() == Perfil.ALUNO)
                .collect(Collectors.toList());
        model.addAttribute("alunos", alunos);
        model.addAttribute("provas", provaService.listarTodos());
        model.addAttribute("respostaAlunoDTO", respostaAlunoDTO);
        return "resposta/formulario";
    }

    @PostMapping("salvar")
    public String salvar(@ModelAttribute RespostaAlunoDTO respostaAlunoDTO, Model model) {
        try {
            respostaAlunoService.salvar(respostaAlunoDTO);
            return "redirect:/resposta/listar";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errotitulo", "Erro ao salvar respostas");
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("alunos", usuarioService.listarTodos());
            model.addAttribute("provas", provaService.listarTodos());
            return "resposta/formulario";
        }
    }

    @GetMapping("listar")
    public String listar(Model model) {
        var respostas = respostaAlunoService.listarTodos()
                .stream()
                .map(r -> modelMapper.map(r, RespostaAlunoDTO.class))
                .collect(Collectors.toList());
        model.addAttribute("respostas", respostas);
        return "resposta/lista";
    }
}