package hackathon.backend.dto;

import lombok.Data;

@Data
public class NotaAlunoDTO {
    private String nomeAluno;
    private String turma;
    private String tituloProva;
    private String disciplina;
    private String dataProva;
    private Double nota;
}
