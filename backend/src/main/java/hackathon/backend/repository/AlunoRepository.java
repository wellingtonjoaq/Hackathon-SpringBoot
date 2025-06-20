package hackathon.backend.repository;

import hackathon.backend.model.Aluno;
import hackathon.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Aluno findByUsuario(Usuario usuario);
}
