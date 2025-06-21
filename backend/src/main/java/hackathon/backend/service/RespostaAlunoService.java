package hackathon.backend.service;

import hackathon.backend.model.Prova;
import hackathon.backend.model.ProvaGabarito;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.model.RespostaAlunoDetalhe;
import hackathon.backend.repository.ProvaRepository;
import hackathon.backend.repository.RespostaAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RespostaAlunoService {

    @Autowired
    private RespostaAlunoRepository repository;

    @Autowired
    private ProvaRepository provaRepository;

    @Transactional
    public void salvar(RespostaAluno resposta) {
        Prova prova = provaRepository.findById(resposta.getProva().getId())
                .orElseThrow(() -> new RuntimeException("Prova não encontrada"));

        int acertos = 0;

        for (RespostaAlunoDetalhe detalhe : resposta.getDetalhes()) {
            ProvaGabarito gabarito = prova.getGabarito().stream()
                    .filter(g -> g.getNumeroQuestao() == detalhe.getNumeroQuestao())
                    .findFirst()
                    .orElse(null);

            if (gabarito != null && detalhe.getResposta().equalsIgnoreCase(gabarito.getRespostaCorreta())) {
                acertos++;
            }
        }

        int totalQuestoes = prova.getGabarito().size();
        double nota = (double) acertos / totalQuestoes * 10.0;
        resposta.setNota(nota);

        repository.save(resposta);
    }

    public List<RespostaAluno> listarTodos() {
        return repository.findAll();
    }
}
