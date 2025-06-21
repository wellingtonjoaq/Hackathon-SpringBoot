package hackathon.backend.controller;

import hackathon.backend.dto.GabaritoDTO;
import hackathon.backend.dto.ProvaDTO;
import hackathon.backend.service.DisciplinaService;
import hackathon.backend.service.ProvaService;
import hackathon.backend.service.TurmaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping({"", "/{id}"})
    public String iniciar(@PathVariable(required = false) Long id, ProvaDTO provaDTO, Model model) {
        if (id != null) {
            var prova = provaService.buscarPorId(id);
            provaDTO = modelMapper.map(prova, ProvaDTO.class);
            if (provaDTO.getGabarito() == null) {
                provaDTO.setGabarito(new ArrayList<>());
            }
        } else {
            if (provaDTO.getGabarito() == null) {
                provaDTO.setGabarito(new ArrayList<>());
            }
        }

        model.addAttribute("turmas", turmaService.listarTodos());
        model.addAttribute("disciplinas", disciplinaService.listarTodos());
        model.addAttribute("provaDTO", provaDTO);
        return "prova/formulario";
    }

    @PostMapping("/salvar")
    public String processarFormulario(@ModelAttribute ProvaDTO provaDTO,
                                      @RequestParam(value = "action", required = false) String action,
                                      @RequestParam(value = "novaQuestaoNumero", required = false) Integer novaQuestaoNumero,
                                      @RequestParam(value = "novaQuestaoResposta", required = false) String novaQuestaoResposta,
                                      @RequestParam(value = "removeIndex", required = false, defaultValue = "-1") int removeIndex,
                                      Model model) {

        if (provaDTO.getGabarito() == null) {
            provaDTO.setGabarito(new ArrayList<>());
        }

        if ("adicionar".equals(action)) {
            if (novaQuestaoNumero != null && !novaQuestaoResposta.isBlank()) {
                GabaritoDTO novaQuestao = new GabaritoDTO();
                novaQuestao.setNumeroQuestao(novaQuestaoNumero);
                novaQuestao.setRespostaCorreta(novaQuestaoResposta.toUpperCase().trim());

                boolean questaoExiste = provaDTO.getGabarito().stream()
                        .anyMatch(g -> g.getNumeroQuestao().equals(novaQuestao.getNumeroQuestao()));
                if (!questaoExiste) {
                    provaDTO.getGabarito().add(novaQuestao);
                } else {
                    model.addAttribute("erroQuestao", "Questão " + novaQuestao.getNumeroQuestao() + " já existe no gabarito.");
                }
            } else {
                model.addAttribute("erroQuestao", "Número da questão e resposta correta são obrigatórios para adicionar uma questão.");
            }
            model.addAttribute("turmas", turmaService.listarTodos());
            model.addAttribute("disciplinas", disciplinaService.listarTodos());
            return "prova/formulario";

        } else if ("remover".equals(action)) {
            if (provaDTO.getGabarito() != null && removeIndex >= 0 && removeIndex < provaDTO.getGabarito().size()) {
                provaDTO.getGabarito().remove(removeIndex);
            }
            model.addAttribute("turmas", turmaService.listarTodos());
            model.addAttribute("disciplinas", disciplinaService.listarTodos());
            return "prova/formulario";

        } else {
            System.out.println("ProvaDTO recebido para salvar: " + provaDTO);
            try {
                if (provaDTO.getGabarito() != null) {
                    provaDTO.getGabarito().forEach(g -> g.setRespostaCorreta(g.getRespostaCorreta().toUpperCase().trim()));
                }
                provaService.salvarProvaComGabarito(provaDTO);
                return "redirect:/prova/listar";
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("errotitulo", "Erro ao salvar prova");
                model.addAttribute("erro", e.getMessage());
                model.addAttribute("turmas", turmaService.listarTodos());
                model.addAttribute("disciplinas", disciplinaService.listarTodos());
                return "prova/formulario";
            }
        }
    }

    @GetMapping("listar")
    public String listar(Model model) {
        var provas = provaService.listarTodos()
                .stream()
                .map(p -> {
                    ProvaDTO dto = modelMapper.map(p, ProvaDTO.class);
                    if (p.getTurma() != null) {
                        dto.setNomeTurma(p.getTurma().getNome());
                    }
                    if (p.getDisciplina() != null) {
                        dto.setNomeDisciplina(p.getDisciplina().getNome());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        model.addAttribute("provas", provas);
        return "prova/lista";
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        provaService.deletarPorId(id);
        return "redirect:/prova/listar";
    }
}