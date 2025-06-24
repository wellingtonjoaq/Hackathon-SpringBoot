package hackathon.backend.model;

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

    private String titulo;

    @NotNull
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @OneToMany(mappedBy = "prova", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProvaGabarito> gabarito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Bimestre bimestre;

}