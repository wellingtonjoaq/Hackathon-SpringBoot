package hackathon.backend.controller;

import hackathon.backend.dto.RespostaAlunoDTO;
import hackathon.backend.dto.RespostaAlunoDetalheDTO;
import hackathon.backend.model.Perfil;
import hackathon.backend.service.DisciplinaService;
import hackathon.backend.service.ProvaService;
import hackathon.backend.service.RespostaAlunoService;
import hackathon.backend.service.TurmaService;
import hackathon.backend.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
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
    private DisciplinaService disciplinaService;

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping({"", "/{id}"})
    public String iniciar(@PathVariable(required = false) Long id, RespostaAlunoDTO respostaAlunoDTO, Model model) {
        if (id != null) {
            respostaAlunoDTO = respostaAlunoService.buscarPorId(id);
        } else {
            if (respostaAlunoDTO.getDetalhes() == null) {
                respostaAlunoDTO.setDetalhes(new ArrayList<>());
            }
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
            if (respostaAlunoDTO.getDetalhes() == null) {
                respostaAlunoDTO.setDetalhes(new ArrayList<>());
            }

            var alunos = usuarioService.listarTodos()
                    .stream()
                    .filter(u -> u.getPerfil() == Perfil.ALUNO)
                    .collect(Collectors.toList());

            if ("adicionar".equals(respostaAlunoDTO.getAction())) {
                if (respostaAlunoDTO.getNovaQuestaoNumero() != null &&
                        respostaAlunoDTO.getNovaQuestaoResposta() != null &&
                        !respostaAlunoDTO.getNovaQuestaoResposta().trim().isEmpty()) {

                    RespostaAlunoDetalheDTO novoDetalhe = new RespostaAlunoDetalheDTO();
                    novoDetalhe.setNumeroQuestao(respostaAlunoDTO.getNovaQuestaoNumero());
                    novoDetalhe.setResposta(respostaAlunoDTO.getNovaQuestaoResposta().trim());
                    respostaAlunoDTO.getDetalhes().add(novoDetalhe);

                    respostaAlunoDTO.setNovaQuestaoNumero(null);
                    respostaAlunoDTO.setNovaQuestaoResposta(null);
                } else {
                    model.addAttribute("erroQuestao", "Número da Questão e Resposta são obrigatórios para adicionar.");
                }
            } else if ("remover".equals(respostaAlunoDTO.getAction())) {
                if (respostaAlunoDTO.getRemoveIndex() != null &&
                        respostaAlunoDTO.getRemoveIndex() >= 0 &&
                        respostaAlunoDTO.getRemoveIndex() < respostaAlunoDTO.getDetalhes().size()) {
                    respostaAlunoDTO.getDetalhes().remove((int) respostaAlunoDTO.getRemoveIndex());
                } else {
                    model.addAttribute("erroQuestao", "Índice de remoção inválido.");
                }
            } else if ("salvar".equals(respostaAlunoDTO.getAction())) {
                respostaAlunoService.salvar(respostaAlunoDTO);
                return "redirect:/resposta/listar";
            } else {
                model.addAttribute("erro", "Ação inválida.");
            }

            if (respostaAlunoDTO.getProvaId() != null) {
                var gabarito = provaService.buscarPorId(respostaAlunoDTO.getProvaId()).getGabarito();
                Map<Integer, String> gabaritoMap = gabarito.stream()
                        .collect(Collectors.toMap(g -> g.getNumeroQuestao(), g -> g.getRespostaCorreta()));

                for (RespostaAlunoDetalheDTO detalhe : respostaAlunoDTO.getDetalhes()) {
                    String respostaCorreta = gabaritoMap.get(detalhe.getNumeroQuestao());
                    detalhe.setRespostaCorreta(respostaCorreta);
                    if (respostaCorreta != null && detalhe.getResposta() != null) {
                        detalhe.setCorreta(detalhe.getResposta().trim().equalsIgnoreCase(respostaCorreta.trim()));
                    } else {
                        detalhe.setCorreta(false);
                    }
                }
            }

            model.addAttribute("alunos", alunos);
            model.addAttribute("provas", provaService.listarTodos());
            return "resposta/formulario";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errotitulo", "Erro ao processar respostas");
            model.addAttribute("erro", e.getMessage());

            var alunosErro = usuarioService.listarTodos()
                    .stream()
                    .filter(u -> u.getPerfil() == Perfil.ALUNO)
                    .collect(Collectors.toList());
            model.addAttribute("alunos", alunosErro);
            model.addAttribute("provas", provaService.listarTodos());

            return "resposta/formulario";
        }
    }

    @GetMapping("listar")
    public String listar(
            @RequestParam(required = false) Long turmaId,
            @RequestParam(required = false) Long disciplinaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataProva,
            Model model) {

        var respostas = respostaAlunoService.listarPorFiltros(turmaId, disciplinaId, dataProva)
                .stream()
                .map(r -> {
                    var dto = modelMapper.map(r, RespostaAlunoDTO.class);

                    if (r.getAluno() != null) {
                        dto.setAlunoNome(r.getAluno().getNome());
                    }
                    if (r.getProva() != null) {
                        dto.setProvaTitulo(r.getProva().getTitulo());

                        dto.setProvaData(r.getProva().getData());
                    }

                    if (r.getProva() != null && r.getProva().getData() != null) {
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        model.addAttribute("respostas", respostas);
        model.addAttribute("turmaId", turmaId);
        model.addAttribute("disciplinaId", disciplinaId);
        model.addAttribute("dataProva", dataProva);

        var alunos = usuarioService.listarTodos()
                .stream()
                .filter(u -> u.getPerfil() == Perfil.ALUNO)
                .collect(Collectors.toList());
        model.addAttribute("alunos", alunos);

        model.addAttribute("provas", provaService.listarTodos());
        model.addAttribute("turmas", turmaService.listarTodos());
        model.addAttribute("disciplinas", disciplinaService.listarTodos());

        return "resposta/lista";
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        respostaAlunoService.deletarPorId(id);
        return "redirect:/resposta/listar";
    }
}