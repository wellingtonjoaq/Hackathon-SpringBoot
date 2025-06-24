package hackathon.backend.dto;

import lombok.Data;

@Data
public class NotaAlunoDTO {
    private Long alunoId;
    private String nomeAluno;
    private Long turmaId;
    private String nomeTurma;
    private String nomeDisciplina;
    private Double notaPrimeiroBimestre;
    private Double notaSegundoBimestre;
    private Double mediaFinal;
}