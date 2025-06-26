package hackathon.backend.service;

import hackathon.backend.dto.NotaAlunoDTO;
import hackathon.backend.model.Bimestre;
import hackathon.backend.model.Disciplina;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.model.Turma;
import hackathon.backend.repository.DisciplinaRepository;
import hackathon.backend.repository.NotaRepository;
import hackathon.backend.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotaService {

    @Autowired
    private NotaRepository repository;

    @Autowired
    private TurmaRepository turmaRepository; // Necessário para buscar Turma por nome

    @Autowired
    private DisciplinaRepository disciplinaRepository; // Necessário para buscar Disciplina por nome

    public List<NotaAlunoDTO> listarNotasFiltradas(Long professorId, String filtroTurmaNome, String filtroDisciplinaNome, LocalDate dataProva) {
        Long turmaId = null;
        if (filtroTurmaNome != null && !filtroTurmaNome.isBlank()) {
            Turma turma = turmaRepository.findByNome(filtroTurmaNome);
            if (turma != null) {
                turmaId = turma.getId();
            }
        }

        Long disciplinaId = null;
        if (filtroDisciplinaNome != null && !filtroDisciplinaNome.isBlank()) {
            Disciplina disciplina = disciplinaRepository.findByNome(filtroDisciplinaNome);
            if (disciplina != null) {
                disciplinaId = disciplina.getId();
            }
        }

        List<RespostaAluno> respostas = repository.buscarNotasFiltradasPorProfessor(professorId, turmaId, disciplinaId, dataProva);

        Map<String, NotaAlunoDTO> notasAgrupadas = new HashMap<>();

        for (RespostaAluno r : respostas) {
            String turmaNome = r.getProva().getTurma().getNome();
            String disciplinaNome = r.getProva().getDisciplina().getNome();

            String key = r.getAluno().getId() + "-" + r.getProva().getTurma().getId() + "-" + r.getProva().getDisciplina().getId();
            NotaAlunoDTO dto = notasAgrupadas.get(key);

            if (dto == null) {
                dto = new NotaAlunoDTO();
                dto.setAlunoId(r.getAluno().getId());
                dto.setNomeAluno(r.getAluno().getNome());
                dto.setTurmaId(r.getProva().getTurma().getId());
                dto.setNomeTurma(turmaNome);
                dto.setNomeDisciplina(disciplinaNome);
                notasAgrupadas.put(key, dto);
            }

            if (r.getProva().getBimestre() == Bimestre.PRIMEIRO) {
                dto.setNotaPrimeiroBimestre(r.getNota());
            } else if (r.getProva().getBimestre() == Bimestre.SEGUNDO) {
                dto.setNotaSegundoBimestre(r.getNota());
            }
        }

        List<NotaAlunoDTO> resultadosFinais = new ArrayList<>(notasAgrupadas.values());

        for (NotaAlunoDTO dto : resultadosFinais) {
            Double nota1 = dto.getNotaPrimeiroBimestre();
            Double nota2 = dto.getNotaSegundoBimestre();

            if (nota1 != null && nota2 != null) {
                dto.setMediaFinal((nota1 + nota2) / 2.0);
            } else if (nota1 != null) {
                dto.setMediaFinal(nota1);
            } else if (nota2 != null) {
                dto.setMediaFinal(nota2);
            } else {
                dto.setMediaFinal(null);
            }
        }

        return resultadosFinais;
    }

    public List<String> listarTurmasDoProfessor(Long professorId) {
        return repository.findDistinctTurmasByProfessorId(professorId)
                .stream()
                .map(Turma::getNome)
                .collect(Collectors.toList());
    }

    public List<String> listarDisciplinasDoProfessor(Long professorId) {
        return repository.findDistinctDisciplinasByProfessorId(professorId)
                .stream()
                .map(Disciplina::getNome)
                .collect(Collectors.toList());
    }
}