package hackathon.backend.dto;

import lombok.Data;

@Data
public class NotaAlunoDetalheDTO {
    private Long provaId;
    private String tituloProva;
    private String nomeDisciplina;
    private String nomeTurma;
    private String bimestre;
    private String dataProva;
    private Double notaObtida;
}