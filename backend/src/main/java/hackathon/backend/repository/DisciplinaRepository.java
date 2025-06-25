package hackathon.backend.repository;

import hackathon.backend.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {

    Disciplina findByNome(String nome);

    List<Disciplina> findByProfessorId(Long professorId);

    @Query("SELECT d FROM Disciplina d " +
            "LEFT JOIN d.professor p " +
            "WHERE (:nome IS NULL OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
            "AND (:professor IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :professor, '%')))")
    List<Disciplina> buscarComFiltros(@Param("nome") String nome, @Param("professor") String professor);

    @Query("SELECT DISTINCT p.nome FROM Disciplina d LEFT JOIN d.professor p WHERE p IS NOT NULL")
    List<String> listarProfessoresUnicos();
}
