package hackathon.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class RespostaAlunoDTO {
    private Long id;
    private Long alunoId;
    private Long provaId;
    private List<String> respostas;
    private Double nota;
}