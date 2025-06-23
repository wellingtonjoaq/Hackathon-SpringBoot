package hackathon.backend.repository;

import hackathon.backend.model.Prova;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProvaRepository extends JpaRepository<Prova, Long> {

    @Query("SELECT p FROM Prova p " +
            "WHERE (:turmaId IS NULL OR p.turma.id = :turmaId) " +
            "AND (:disciplinaId IS NULL OR p.disciplina.id = :disciplinaId) " +
            "AND (:data IS NULL OR p.data = :data)")
    List<Prova> buscarPorFiltros(@Param("turmaId") Long turmaId,
                                 @Param("disciplinaId") Long disciplinaId,
                                 @Param("data") LocalDate data);
}
