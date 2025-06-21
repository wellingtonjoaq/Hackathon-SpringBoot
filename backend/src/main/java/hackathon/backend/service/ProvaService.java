package hackathon.backend.service;

import hackathon.backend.dto.ProvaDTO;
import hackathon.backend.model.Disciplina;
import hackathon.backend.model.Prova;
import hackathon.backend.model.ProvaGabarito;
import hackathon.backend.model.Turma;
import hackathon.backend.repository.DisciplinaRepository;
import hackathon.backend.repository.ProvaRepository;
import hackathon.backend.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProvaService {

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Transactional
    public void salvarProvaComGabarito(ProvaDTO dto) {
        Turma turma = turmaRepository.findById(dto.getTurmaId()).orElseThrow();
        Disciplina disciplina = disciplinaRepository.findById(dto.getDisciplinaId()).orElseThrow();

        Prova prova = new Prova();
        prova.setTitulo(dto.getTitulo());
        prova.setData(dto.getData());
        prova.setTurma(turma);
        prova.setDisciplina(disciplina);

        List<ProvaGabarito> gabaritoList = dto.getGabarito().stream().map(g -> {
            ProvaGabarito gab = new ProvaGabarito();
            gab.setProva(prova);
            gab.setNumeroQuestao(g.getNumeroQuestao());
            gab.setRespostaCorreta(g.getRespostaCorreta());
            return gab;
        }).toList();

        prova.setGabarito(gabaritoList);

        provaRepository.save(prova);
    }


    public List<Prova> listarTodos() {
        return provaRepository.findAll();
    }

    public Prova buscarPorId(Long id) {
        return provaRepository.findById(id).orElseThrow();
    }

    public void deletarPorId(Long id) {
        provaRepository.deleteById(id);
    }
}
