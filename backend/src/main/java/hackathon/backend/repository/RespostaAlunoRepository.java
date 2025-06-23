package hackathon.backend.repository;

import hackathon.backend.model.RespostaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RespostaAlunoRepository extends JpaRepository<RespostaAluno, Long> {

    @Query("SELECT r FROM RespostaAluno r " +
            "WHERE (:alunoId IS NULL OR r.aluno.id = :alunoId) " +
            "AND (:provaId IS NULL OR r.prova.id = :provaId)")
    List<RespostaAluno> findByAlunoIdAndProvaId(
            @Param("alunoId") Long alunoId,
            @Param("provaId") Long provaId);
}
