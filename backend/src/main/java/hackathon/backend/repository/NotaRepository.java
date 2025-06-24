package hackathon.backend.repository;

import hackathon.backend.model.RespostaAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotaRepository extends JpaRepository<RespostaAluno, Long> {

    @Query("""
            SELECT ra
            FROM RespostaAluno ra
            JOIN FETCH ra.aluno a
            JOIN FETCH ra.prova p
            JOIN FETCH p.disciplina d
            JOIN FETCH p.turma t
            WHERE d.professor.id = :professorId
            ORDER BY a.nome, t.nome, d.nome, p.bimestre
            """)
    List<RespostaAluno> buscarTodasNotasComDetalhesPorProfessor(@Param("professorId") Long professorId);

}