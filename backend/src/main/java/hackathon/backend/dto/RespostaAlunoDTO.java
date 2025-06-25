package hackathon.backend.dto;

import lombok.Data;

import java.time.LocalDate;
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
    private String alunoNome;
    private String provaTitulo;
    private LocalDate provaData;

}