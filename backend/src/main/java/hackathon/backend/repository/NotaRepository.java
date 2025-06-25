package hackathon.backend.repository;

import hackathon.backend.model.Disciplina;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<RespostaAluno, Long> {

    @Query("""
            SELECT ra
            FROM RespostaAluno ra
            JOIN FETCH ra.aluno a
            JOIN FETCH ra.prova p
            JOIN FETCH p.disciplina d
            JOIN FETCH p.turma t
            WHERE d.professor.id = :professorId
            AND (:turmaId IS NULL OR t.id = :turmaId)
            AND (:disciplinaId IS NULL OR d.id = :disciplinaId)
            AND (:dataProva IS NULL OR p.data = :dataProva)
            ORDER BY a.nome, t.nome, d.nome, p.bimestre
            """)
    List<RespostaAluno> buscarNotasFiltradasPorProfessor(
            @Param("professorId") Long professorId,
            @Param("turmaId") Long turmaId,
            @Param("disciplinaId") Long disciplinaId,
            @Param("dataProva") LocalDate dataProva);

    @Query("""
            SELECT DISTINCT t
            FROM RespostaAluno ra
            JOIN ra.prova p
            JOIN p.disciplina d
            JOIN p.turma t
            WHERE d.professor.id = :professorId
            ORDER BY t.nome
            """)
    List<Turma> findDistinctTurmasByProfessorId(@Param("professorId") Long professorId);

    @Query("""
            SELECT DISTINCT d
            FROM RespostaAluno ra
            JOIN ra.prova p
            JOIN p.disciplina d
            WHERE d.professor.id = :professorId
            ORDER BY d.nome
            """)
    List<Disciplina> findDistinctDisciplinasByProfessorId(@Param("professorId") Long professorId);
}