package hackathon.backend.repository;

import hackathon.backend.model.RespostaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RespostaAlunoRepository extends JpaRepository<RespostaAluno, Long> {
    boolean existsByProvaId(Long provaId);

    @Query("""
            SELECT ra
            FROM RespostaAluno ra
            JOIN FETCH ra.aluno a
            JOIN FETCH ra.prova p
            JOIN FETCH p.disciplina d
            JOIN FETCH p.turma t
            LEFT JOIN FETCH p.gabarito g
            WHERE a.id = :alunoId
            ORDER BY p.data DESC, p.titulo ASC
            """)
    List<RespostaAluno> findByAlunoId(@Param("alunoId") Long alunoId);
}