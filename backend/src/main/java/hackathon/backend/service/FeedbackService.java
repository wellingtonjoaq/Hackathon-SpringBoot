package hackathon.backend.service;

import hackathon.backend.dto.FeedbackDTO;
import hackathon.backend.dto.FeedbackQuestaoDTO;
import hackathon.backend.model.ProvaGabarito;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.model.RespostaAlunoDetalhe;
import hackathon.backend.repository.RespostaAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private RespostaAlunoRepository respostaAlunoRepository;

    public List<FeedbackDTO> listarFeedbacksDoAluno(Long alunoId) {
        List<RespostaAluno> respostas = respostaAlunoRepository.findByAlunoId(alunoId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<Long, RespostaAluno> respostasPorProvaId = respostas.stream()
                .collect(Collectors.toMap(r -> r.getProva().getId(), r -> r));


        List<FeedbackDTO> feedbacks = new ArrayList<>();

        for (Map.Entry<Long, RespostaAluno> entry : respostasPorProvaId.entrySet()) {
            RespostaAluno respostaAlunoPrincipal = entry.getValue();

            FeedbackDTO feedback = new FeedbackDTO();
            feedback.setProvaId(respostaAlunoPrincipal.getProva().getId());
            feedback.setTituloProva(respostaAlunoPrincipal.getProva().getTitulo());
            feedback.setNomeDisciplina(respostaAlunoPrincipal.getProva().getDisciplina().getNome());
            feedback.setNomeTurma(respostaAlunoPrincipal.getProva().getTurma().getNome());
            feedback.setDataProva(respostaAlunoPrincipal.getProva().getData().format(formatter));
            feedback.setNotaFinal(respostaAlunoPrincipal.getNota());

            List<FeedbackQuestaoDTO> questoesFeedback = new ArrayList<>();

            Map<Integer, ProvaGabarito> gabaritoMap = respostaAlunoPrincipal.getProva().getGabarito().stream()
                    .collect(Collectors.toMap(ProvaGabarito::getNumeroQuestao, g -> g));

            for (RespostaAlunoDetalhe detalhe : respostaAlunoPrincipal.getDetalhes()) {
                FeedbackQuestaoDTO questaoDto = new FeedbackQuestaoDTO();
                questaoDto.setNumeroQuestao(detalhe.getNumeroQuestao());
                questaoDto.setRespostaDadaAluno(detalhe.getResposta());

                ProvaGabarito gabaritoQuestao = gabaritoMap.get(detalhe.getNumeroQuestao());
                if (gabaritoQuestao != null) {
                    questaoDto.setRespostaCorreta(gabaritoQuestao.getRespostaCorreta());
                    questaoDto.setCorreta(detalhe.getResposta().equalsIgnoreCase(gabaritoQuestao.getRespostaCorreta()));
                } else {
                    questaoDto.setRespostaCorreta("N/A");
                    questaoDto.setCorreta(false);
                }
                questoesFeedback.add(questaoDto);
            }

            questoesFeedback.sort(Comparator.comparing(FeedbackQuestaoDTO::getNumeroQuestao));
            feedback.setQuestoes(questoesFeedback);
            feedbacks.add(feedback);
        }

        return feedbacks;
    }
}