package hackathon.backend.repository;

import hackathon.backend.model.Bimestre;
import hackathon.backend.model.Prova;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProvaRepository extends JpaRepository<Prova, Long> {

    List<Prova> findByDisciplinaIdAndBimestreAndIdIsNot(Long disciplinaId, Bimestre bimestre, Long id);

    List<Prova> findByDisciplinaIdAndBimestre(Long disciplinaId, Bimestre bimestre);

    List<Prova> findByDisciplinaIdAndTurmaIdAndBimestre(Long disciplinaId, Long turmaId, Bimestre bimestre);

    List<Prova> findByDisciplinaIdAndTurmaIdAndBimestreAndIdIsNot(Long disciplinaId, Long turmaId, Bimestre bimestre, Long id);

    @Query("SELECT p FROM Prova p " +
            "WHERE (:turmaId IS NULL OR p.turma.id = :turmaId) " +
            "AND (:disciplinaId IS NULL OR p.disciplina.id = :disciplinaId) " +
            "AND (:bimestre IS NULL OR p.bimestre = :bimestre) " +
            "AND (:data IS NULL OR p.data = :data)")
    List<Prova> buscarPorFiltros(@Param("turmaId") Long turmaId,
                                 @Param("disciplinaId") Long disciplinaId,
                                 @Param("bimestre") Bimestre bimestre,
                                 @Param("data") LocalDate data);
}