package hackathon.backend.service;

import hackathon.backend.dto.NotaAlunoDetalheDTO;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.repository.RespostaAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.OptionalDouble; // Importe esta classe
import java.util.stream.Collectors;

@Service
public class NotaAlunoService {

    @Autowired
    private RespostaAlunoRepository respostaAlunoRepository;

    public List<NotaAlunoDetalheDTO> listarNotasDoAluno(Long alunoId) {
        List<RespostaAluno> respostas = respostaAlunoRepository.findByAlunoId(alunoId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return respostas.stream().map(r -> {
            NotaAlunoDetalheDTO dto = new NotaAlunoDetalheDTO();
            dto.setProvaId(r.getProva().getId());
            dto.setTituloProva(r.getProva().getTitulo());
            dto.setNomeDisciplina(r.getProva().getDisciplina().getNome());
            dto.setNomeTurma(r.getProva().getTurma().getNome());
            dto.setBimestre(r.getProva().getBimestre() != null ? r.getProva().getBimestre().name() : "N/A");
            dto.setDataProva(r.getProva().getData() != null ? r.getProva().getData().format(formatter) : "N/A");
            dto.setNotaObtida(r.getNota());
            return dto;
        }).collect(Collectors.toList());
    }

    public Double calcularMediaNotasDoAluno(Long alunoId) {
        List<RespostaAluno> respostas = respostaAlunoRepository.findByAlunoId(alunoId);

        OptionalDouble media = respostas.stream()
                .mapToDouble(RespostaAluno::getNota)
                .average();

        return media.isPresent() ? media.getAsDouble() : null;
    }
}