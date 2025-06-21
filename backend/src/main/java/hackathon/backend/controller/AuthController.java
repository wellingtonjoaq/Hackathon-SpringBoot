package hackathon.backend.controller;

import hackathon.backend.dto.UsuarioDTO;
import hackathon.backend.exception.UnauthorizedException;
import hackathon.backend.model.Usuario;
import hackathon.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // Permite requisições de qualquer origem (teste; em produção, restringir)
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public UsuarioDTO login(@RequestBody UsuarioDTO loginDTO) {
        // Busca usuário pelo email
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuário ou senha inválidos"));

        // Verifica senha com bcrypt
        if (passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha())) {
            // Retorna DTO com dados do usuário (sem senha)
            return new UsuarioDTO(usuario);
        } else {
            throw new UnauthorizedException("Usuário ou senha inválidos");
        }
    }
}
