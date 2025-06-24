package hackathon.backend.service;

import hackathon.backend.dto.NotaAlunoDTO;
import hackathon.backend.model.Bimestre;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class NotaService {

    @Autowired
    private NotaRepository repository;

    public List<NotaAlunoDTO> listarNotasPorProfessor(Long professorId) {
        List<RespostaAluno> respostas = repository.buscarTodasNotasComDetalhesPorProfessor(professorId);

        Map<String, NotaAlunoDTO> notasAgrupadas = new HashMap<>();

        for (RespostaAluno r : respostas) {
            String key = r.getAluno().getId() + "-" + r.getProva().getTurma().getId() + "-" + r.getProva().getDisciplina().getId();

            NotaAlunoDTO dto = notasAgrupadas.get(key);
            if (dto == null) {
                dto = new NotaAlunoDTO();
                dto.setAlunoId(r.getAluno().getId());
                dto.setNomeAluno(r.getAluno().getNome());
                dto.setTurmaId(r.getProva().getTurma().getId());
                dto.setNomeTurma(r.getProva().getTurma().getNome());
                dto.setNomeDisciplina(r.getProva().getDisciplina().getNome());
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
}