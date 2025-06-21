package hackathon.backend.dto;

import lombok.Data;

@Data
public class DisciplinaDTO {
    private Long id;
    private String nome;
    private Long professorId;
    private String nomeProfessor;
}
