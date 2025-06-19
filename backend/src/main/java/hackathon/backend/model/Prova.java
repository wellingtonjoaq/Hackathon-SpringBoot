package hackathon.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Prova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Informe a data da prova")
    private LocalDate data;

    @NotNull(message = "Informe a turma")
    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    @JsonBackReference
    private Turma turma;

    @NotNull(message = "Informe a disciplina")
    @ManyToOne
    @JoinColumn(name = "disciplina_id", nullable = false)
    @JsonBackReference
    private Disciplina disciplina;

    @ElementCollection
    @CollectionTable(name = "prova_gabarito", joinColumns = @JoinColumn(name = "prova_id"))
    @Column(name = "resposta_correta")
    private List<String> gabarito;
}
