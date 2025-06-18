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
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Assumindo que nome e demais dados ficam no Usuario, então só linkamos aqui:
    @NotNull(message = "Informe o usuário associado ao aluno")
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @NotNull(message = "Informe a turma")
    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    @JsonBackReference  // Para evitar ciclo de serialização com Turma -> Aluno
    private Turma turma;
}
