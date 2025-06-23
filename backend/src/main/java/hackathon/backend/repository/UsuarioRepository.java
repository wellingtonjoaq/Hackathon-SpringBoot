package hackathon.backend.repository;

import hackathon.backend.model.Perfil;
import hackathon.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByPerfil(Perfil perfil);
}
