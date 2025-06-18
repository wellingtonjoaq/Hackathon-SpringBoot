package hackathon.backend.dto;

import lombok.Data;

@Data
public class AlunoDTO {
    private Long id;
    private String nome;
    private Long usuarioId;
    private Long turmaId;
}
