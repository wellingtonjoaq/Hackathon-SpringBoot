package hackathon.backend.repository;

import hackathon.backend.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
}
