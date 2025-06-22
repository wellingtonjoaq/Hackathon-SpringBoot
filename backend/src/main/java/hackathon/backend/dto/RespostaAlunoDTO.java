package hackathon.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class RespostaAlunoDTO {
    private Long id;
    private Long alunoId;
    private Long provaId;
    private List<RespostaAlunoDetalheDTO> detalhes;
    private Double nota;

    private Integer novaQuestaoNumero;
    private String novaQuestaoResposta;
    private Integer removeIndex;
    private String action; 
}