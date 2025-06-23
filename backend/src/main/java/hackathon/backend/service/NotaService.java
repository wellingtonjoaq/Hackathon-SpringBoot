package hackathon.backend.service;

import hackathon.backend.dto.NotaAlunoDTO;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.repository.NotaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class NotaService {

    @Autowired
    private NotaRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    public List<NotaAlunoDTO> listarNotasPorProfessor(Long professorId) {
        List<RespostaAluno> respostas = repository.buscarNotasPorProfessor(professorId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return respostas.stream().map(r -> {
            NotaAlunoDTO dto = new NotaAlunoDTO();
            dto.setNomeAluno(r.getAluno().getNome());
            dto.setTurma(r.getProva().getTurma().getNome());
            dto.setTituloProva(r.getProva().getTitulo());
            dto.setDisciplina(r.getProva().getDisciplina().getNome());
            dto.setDataProva(r.getProva().getData().format(formatter));
            dto.setNota(r.getNota());
            return dto;
        }).collect(Collectors.toList());
    }
}
