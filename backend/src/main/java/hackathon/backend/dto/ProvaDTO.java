package hackathon.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class ProvaDTO {
    private Long id;
    private String titulo;
    private LocalDate data;
    private Long turmaId;
    private Long disciplinaId;

    private String nomeTurma;
    private String nomeDisciplina;

    private List<GabaritoDTO> gabarito;
}