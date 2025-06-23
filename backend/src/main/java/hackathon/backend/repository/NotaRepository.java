package hackathon.backend.repository;

import hackathon.backend.model.RespostaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotaRepository extends JpaRepository<RespostaAluno, Long> {

    @Query("""
            SELECT r 
            FROM RespostaAluno r
            JOIN r.prova p
            JOIN p.disciplina d
            WHERE d.professor.id = :professorId
            """)
    List<RespostaAluno> buscarNotasPorProfessor(@Param("professorId") Long professorId);
}
