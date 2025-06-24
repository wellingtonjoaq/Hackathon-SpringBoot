package hackathon.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class FeedbackDTO {
    private Long provaId;
    private String tituloProva;
    private String nomeDisciplina;
    private String nomeTurma;
    private String dataProva;
    private Double notaFinal;
    private List<FeedbackQuestaoDTO> questoes;
}