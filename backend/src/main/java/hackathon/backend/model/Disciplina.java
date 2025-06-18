package hackathon.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Informe o nome da disciplina")
    private String nome;

    @NotNull(message = "Informe o professor responsável")
    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    @JsonBackReference  // para evitar ciclo na serialização JSON
    private Usuario professor;
}
