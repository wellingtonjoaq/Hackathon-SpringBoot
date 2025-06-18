package hackathon.backend.repository;

import hackathon.backend.model.Prova;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvaRepository extends JpaRepository<Prova, Long> {
}
