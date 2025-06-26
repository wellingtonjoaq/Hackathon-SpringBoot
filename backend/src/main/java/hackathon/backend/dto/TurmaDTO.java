package hackathon.backend.dto;

import lombok.Data;

@Data
public class TurmaDTO {
    private Long id;
    private String nome;
    private String periodo;
    private String curso;
}