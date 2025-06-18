package hackathon.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProvaDTO {
    private Long id;
    private String titulo;
    private LocalDate data;
    private Long turmaId;
    private Long disciplinaId;
    private List<Character> gabarito; // ou List<String> se for alfanumérico
}
