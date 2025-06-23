package hackathon.backend.repository;

import hackathon.backend.model.Perfil;
import hackathon.backend.model.Turma;
import hackathon.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    // Busca usuários por perfil
    List<Usuario> findByPerfil(Perfil perfil);

    // Busca alunos por perfil e turma via join com aluno e turma
    @Query("SELECT u FROM Usuario u JOIN hackathon.backend.model.Aluno a ON a.usuario = u WHERE u.perfil = :perfil AND a.turma = :turma")
    List<Usuario> findByPerfilAndTurma(@Param("perfil") Perfil perfil, @Param("turma") Turma turma);
}
