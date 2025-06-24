package hackathon.backend.service;

import hackathon.backend.dto.NotaAlunoDTO;
import hackathon.backend.model.Bimestre;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotaService {

    @Autowired
    private NotaRepository repository;

    public List<NotaAlunoDTO> listarNotasFiltradas(Long professorId, String filtroTurma, String filtroDisciplina) {
        List<RespostaAluno> respostas = repository.buscarTodasNotasComDetalhesPorProfessor(professorId);

        Map<String, NotaAlunoDTO> notasAgrupadas = new HashMap<>();

        for (RespostaAluno r : respostas) {
            String turmaNome = r.getProva().getTurma().getNome();
            String disciplinaNome = r.getProva().getDisciplina().getNome();

            // Aplica os filtros se houver
            if (filtroTurma != null && !filtroTurma.isBlank() && !turmaNome.equalsIgnoreCase(filtroTurma)) {
                continue;
            }
            if (filtroDisciplina != null && !filtroDisciplina.isBlank() && !disciplinaNome.equalsIgnoreCase(filtroDisciplina)) {
                continue;
            }

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
        List<RespostaAluno> respostas = repository.buscarTodasNotasComDetalhesPorProfessor(professorId);
        Set<String> turmas = new TreeSet<>();
        for (RespostaAluno r : respostas) {
            turmas.add(r.getProva().getTurma().getNome());
        }
        return new ArrayList<>(turmas);
    }

    public List<String> listarDisciplinasDoProfessor(Long professorId) {
        List<RespostaAluno> respostas = repository.buscarTodasNotasComDetalhesPorProfessor(professorId);
        Set<String> disciplinas = new TreeSet<>();
        for (RespostaAluno r : respostas) {
            disciplinas.add(r.getProva().getDisciplina().getNome());
        }
        return new ArrayList<>(disciplinas);
    }
}
