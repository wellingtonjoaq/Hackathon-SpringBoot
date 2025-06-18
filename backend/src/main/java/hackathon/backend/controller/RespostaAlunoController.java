package hackathon.backend.controller;

import hackathon.backend.dto.RespostaAlunoDTO;
import hackathon.backend.service.AlunoService;
import hackathon.backend.service.ProvaService;
import hackathon.backend.service.RespostaAlunoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("resposta")
public class RespostaAlunoController {

    @Autowired
    private RespostaAlunoService respostaAlunoService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private ProvaService provaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping()
    public String iniciar(RespostaAlunoDTO respostaAlunoDTO, Model model) {
        model.addAttribute("alunos", alunoService.listarTodos());
        model.addAttribute("provas", provaService.listarTodos());
        model.addAttribute("respostaAlunoDTO", respostaAlunoDTO);
        return "resposta/formulario";
    }

    @PostMapping("salvar")
    public String salvar(@ModelAttribute RespostaAlunoDTO respostaAlunoDTO, Model model) {
        try {
            respostaAlunoService.salvar(modelMapper.map(respostaAlunoDTO, hackathon.backend.model.RespostaAluno.class));
            return "redirect:/resposta/listar";
        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar respostas");
            model.addAttribute("erro", e.getMessage());
            return iniciar(respostaAlunoDTO, model);
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
