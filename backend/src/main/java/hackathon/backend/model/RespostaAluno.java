package hackathon.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RespostaAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Informe o aluno")
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    @JsonBackReference
    private Aluno aluno;

    @NotNull(message = "Informe a prova")
    @ManyToOne
    @JoinColumn(name = "prova_id", nullable = false)
    @JsonBackReference
    private Prova prova;

    @ElementCollection
    @CollectionTable(name = "respostas_aluno", joinColumns = @JoinColumn(name = "resposta_id"))
    @Column(name = "resposta")
    private List<String> respostas;

    private double nota;
}
