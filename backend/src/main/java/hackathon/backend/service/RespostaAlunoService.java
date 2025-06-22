package hackathon.backend.service;

import hackathon.backend.dto.RespostaAlunoDTO;
import hackathon.backend.dto.RespostaAlunoDetalheDTO;
import hackathon.backend.model.Aluno;
import hackathon.backend.model.Prova;
import hackathon.backend.model.ProvaGabarito;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.model.RespostaAlunoDetalhe;
import hackathon.backend.repository.ProvaRepository;
import hackathon.backend.repository.RespostaAlunoRepository;
import hackathon.backend.repository.AlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class RespostaAlunoService {

    private static final Logger logger = LoggerFactory.getLogger(RespostaAlunoService.class);

    @Autowired
    private RespostaAlunoRepository repository;

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Transactional
    public void salvar(RespostaAlunoDTO dto) {
        logger.debug("Attempting to save RespostaAluno with DTO ID: {}", dto.getId());

        Aluno aluno = alunoRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + dto.getAlunoId()));

        Prova prova = provaRepository.findById(dto.getProvaId())
                .orElseThrow(() -> new RuntimeException("Prova não encontrada com ID: " + dto.getProvaId()));

        RespostaAluno respostaAluno;
        if (dto.getId() != null) {
            respostaAluno = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Resposta do Aluno não encontrada para edição com ID: " + dto.getId()));
            if (respostaAluno.getDetalhes() != null) {
                respostaAluno.getDetalhes().clear();
            }
        } else {
            respostaAluno = new RespostaAluno();
        }

        respostaAluno.setAluno(aluno);
        respostaAluno.setProva(prova);

        int acertos = 0;
        List<RespostaAlunoDetalhe> novosDetalhes = new ArrayList<>();

        if (dto.getDetalhes() != null && !dto.getDetalhes().isEmpty()) {
            for (RespostaAlunoDetalheDTO detalheDto : dto.getDetalhes()) {
                ProvaGabarito gabaritoCorreto = prova.getGabarito().stream()
                        .filter(g -> g.getNumeroQuestao() == detalheDto.getNumeroQuestao())
                        .findFirst()
                        .orElse(null);

                if (gabaritoCorreto != null && detalheDto.getResposta() != null &&
                        detalheDto.getResposta().trim().equalsIgnoreCase(gabaritoCorreto.getRespostaCorreta().trim())) {
                    acertos++;
                }

                RespostaAlunoDetalhe detalheEntidade = new RespostaAlunoDetalhe();
                if (detalheDto.getId() != null) {
                    detalheEntidade.setId(detalheDto.getId());
                }
                detalheEntidade.setNumeroQuestao(detalheDto.getNumeroQuestao());
                detalheEntidade.setResposta(detalheDto.getResposta());
                detalheEntidade.setRespostaAluno(respostaAluno);
                novosDetalhes.add(detalheEntidade);
            }
        }

        respostaAluno.setDetalhes(novosDetalhes);

        int totalQuestoes = prova.getGabarito() != null ? prova.getGabarito().size() : 0;
        double nota = 0.0;
        if (totalQuestoes > 0) {
            nota = (double) acertos / totalQuestoes * 10.0;
        }
        respostaAluno.setNota(nota);

        repository.save(respostaAluno);
        logger.debug("RespostaAluno saved successfully with ID: {}", respostaAluno.getId());
    }

    public List<RespostaAluno> listarTodos() {
        return repository.findAll();
    }

    public RespostaAluno buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resposta do Aluno não encontrada com ID: " + id));
    }
}