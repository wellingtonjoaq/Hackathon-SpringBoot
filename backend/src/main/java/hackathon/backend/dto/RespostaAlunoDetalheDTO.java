package hackathon.backend.dto;

import lombok.Data;

@Data
public class RespostaAlunoDetalheDTO {
    private Long id;
    private int numeroQuestao;
    private String resposta;
}