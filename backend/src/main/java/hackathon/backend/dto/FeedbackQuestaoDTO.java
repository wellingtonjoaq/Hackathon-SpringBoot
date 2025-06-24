package hackathon.backend.dto;

import lombok.Data;

@Data
public class FeedbackQuestaoDTO {
    private Integer numeroQuestao;
    private String respostaCorreta;
    private String respostaDadaAluno;
    private boolean correta;
}