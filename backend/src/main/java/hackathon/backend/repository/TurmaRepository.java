package hackathon.backend.repository;

import hackathon.backend.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {

    Turma findByNome(String nome);

    List<Turma> findByNomeContainingIgnoreCaseAndPeriodoContainingIgnoreCaseAndCursoContainingIgnoreCase(
            String nome, String periodo, String curso
    );

    @Query("SELECT DISTINCT t.periodo FROM Turma t")
    List<String> findDistinctPeriodo();

    @Query("SELECT DISTINCT t.curso FROM Turma t")
    List<String> findDistinctCurso();
}
