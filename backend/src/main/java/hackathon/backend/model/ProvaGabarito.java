package hackathon.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProvaGabarito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Integer numeroQuestao;

    private String respostaCorreta;

    @ManyToOne
    @JoinColumn(name = "prova_id", nullable = false)
    private Prova prova;
}

