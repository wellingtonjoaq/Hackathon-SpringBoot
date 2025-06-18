package hackathon.backend.repository;

import hackathon.backend.model.RespostaAluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespostaAlunoRepository extends JpaRepository<RespostaAluno, Long> {
}
