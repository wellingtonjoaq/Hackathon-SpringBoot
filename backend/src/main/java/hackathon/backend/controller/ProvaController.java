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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

import hackathon.backend.model.Disciplina;
import hackathon.backend.model.Prova;
import hackathon.backend.model.Bimestre;
import java.util.List;
import java.util.Optional;


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
            if (prova.getBimestre() != null) {
                provaDTO.setBimestre(prova.getBimestre().name());
            }
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
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

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
            if (provaDTO.getDisciplinaId() == null || provaDTO.getBimestre() == null || provaDTO.getBimestre().isEmpty() || provaDTO.getTurmaId() == null) {
                model.addAttribute("errotitulo", "Erro de Validação");
                model.addAttribute("erro", "Por favor, selecione a disciplina, o bimestre e a turma.");
                model.addAttribute("turmas", turmaService.listarTodos());
                model.addAttribute("disciplinas", disciplinaService.listarTodos());
                return "prova/formulario";
            }

            Disciplina disciplina = disciplinaService.buscarPorId(provaDTO.getDisciplinaId())
                    .orElseThrow(() -> new IllegalArgumentException("Disciplina não encontrada."));

            Bimestre bimestreEnum = Bimestre.valueOf(provaDTO.getBimestre());

            List<Prova> provasExistentes;
            if (provaDTO.getId() != null) {
                provasExistentes = provaService.buscarPorDisciplinaTurmaEBimestreExcetoId(
                        disciplina.getId(), provaDTO.getTurmaId(), bimestreEnum, provaDTO.getId());
            } else {
                provasExistentes = provaService.buscarPorDisciplinaTurmaEBimestre(
                        disciplina.getId(), provaDTO.getTurmaId(), bimestreEnum);
            }

            if (!provasExistentes.isEmpty()) {
                model.addAttribute("errotitulo", "Erro de Validação");
                model.addAttribute("erro",
                        "Já existe uma prova cadastrada para a disciplina '" + disciplina.getNome() +
                                "' na turma '" + turmaService.buscarPorId(provaDTO.getTurmaId()).getNome() +
                                "' no " + (bimestreEnum == Bimestre.PRIMEIRO ? "Primeiro" : "Segundo") + " Bimestre.");
                model.addAttribute("turmas", turmaService.listarTodos());
                model.addAttribute("disciplinas", disciplinaService.listarTodos());
                return "prova/formulario";
            }

            try {
                provaService.salvarProvaComGabarito(provaDTO);
                redirectAttributes.addFlashAttribute("mensagemSucesso", "Prova salva com sucesso!");
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
    public String listar(@RequestParam(required = false) Long turmaId,
                         @RequestParam(required = false) Long disciplinaId,
                         @RequestParam(required = false) String bimestre,
                         @RequestParam(required = false)
                         @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
                         LocalDate data,
                         Model model) {

        System.out.println("DEBUG: turmaId=" + turmaId + ", disciplinaId=" + disciplinaId + ", bimestre=" + bimestre + ", data=" + data);

        try {
            var provas = provaService.buscarPorFiltros(turmaId, disciplinaId, bimestre, data)
                    .stream()
                    .map(p -> {
                        ProvaDTO dto = modelMapper.map(p, ProvaDTO.class);
                        if (p.getTurma() != null) dto.setNomeTurma(p.getTurma().getNome());
                        if (p.getDisciplina() != null) dto.setNomeDisciplina(p.getDisciplina().getNome());
                        if (p.getBimestre() != null) dto.setBimestre(p.getBimestre().name());
                        return dto;
                    })
                    .collect(Collectors.toList());

            System.out.println("DEBUG: Provas carregadas: " + provas.size());

            model.addAttribute("provas", provas);
            model.addAttribute("turmas", turmaService.listarTodos());
            model.addAttribute("disciplinas", disciplinaService.listarTodos());
            model.addAttribute("turmaId", turmaId);
            model.addAttribute("disciplinaId", disciplinaId);
            model.addAttribute("bimestre", bimestre);
            model.addAttribute("data", data);
            return "prova/lista";
        } catch (Exception e) {
            System.err.println("ERRO ao listar provas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errotitulo", "Erro ao listar provas");
            model.addAttribute("erro", e.getMessage());

            model.addAttribute("turmas", turmaService.listarTodos());
            model.addAttribute("disciplinas", disciplinaService.listarTodos());
            // Para garantir que a página de listar seja carregada, mesmo com erro
            return "prova/lista";
        }
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        provaService.deletarPorId(id);
        return "redirect:/prova/listar";
    }
}