package hackathon.backend.controller;

import hackathon.backend.dto.UsuarioDTO;
import hackathon.backend.exception.UnauthorizedException;
import hackathon.backend.model.Usuario;
import hackathon.backend.repository.UsuarioRepository;
import hackathon.backend.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public UsuarioDTO login(@RequestBody UsuarioDTO loginDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuário ou senha inválidos"));

        if (passwordEncoder.matches(loginDTO.getSenha(), usuario.getSenha())) {
            return new UsuarioDTO(usuario); // Retorna dados do usuário sem a senha
        } else {
            throw new UnauthorizedException("Usuário ou senha inválidos");
        }
    }
}
