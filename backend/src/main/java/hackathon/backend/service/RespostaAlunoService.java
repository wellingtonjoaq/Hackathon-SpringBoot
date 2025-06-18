package hackathon.backend.service;

import hackathon.backend.model.Prova;
import hackathon.backend.model.RespostaAluno;
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
        Prova prova = provaRepository.findById(resposta.getProva().getId()).orElseThrow();

        int total = prova.getGabarito().size();
        int acertos = 0;
        for (int i = 0; i < total; i++) {
            if (i < resposta.getRespostas().size() &&
                    resposta.getRespostas().get(i).equalsIgnoreCase(String.valueOf(prova.getGabarito().get(i)))) {
                acertos++;
            }
        }

        double nota = (double) acertos / total * 10.0;
        resposta.setNota(nota);
        repository.save(resposta);
    }

    public List<RespostaAluno> listarTodos() {
        return repository.findAll();
    }
}
