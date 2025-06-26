package hackathon.backend.controller;

import hackathon.backend.dto.UsuarioDTO;
import hackathon.backend.model.Aluno;
import hackathon.backend.model.Perfil;
import hackathon.backend.model.Turma;
import hackathon.backend.model.Usuario;
import hackathon.backend.repository.AlunoRepository;
import hackathon.backend.repository.TurmaRepository;
import hackathon.backend.repository.UsuarioRepository;
import hackathon.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar(
            @RequestParam(required = false) Perfil perfil,
            @RequestParam(required = false) Long turmaId) {

        List<Usuario> usuarios;
        if (perfil != null || turmaId != null) {
            Turma turmaFiltro = (turmaId != null) ? new Turma(turmaId, null, null, null) : null;
            usuarios = usuarioService.listarComFiltro(perfil, turmaFiltro);
        } else {
            usuarios = usuarioService.listarTodos();
        }

        List<UsuarioDTO> usuariosDTO = usuarios.stream()
                .map(usuario -> {
                    UsuarioDTO dto = new UsuarioDTO(usuario);
                    if (usuario.getPerfil() == Perfil.ALUNO) {
                        Aluno aluno = alunoRepository.findByUsuario(usuario);
                        if (aluno != null && aluno.getTurma() != null) {
                            dto.setTurmaId(aluno.getTurma().getId());
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    UsuarioDTO dto = new UsuarioDTO(usuario);
                    if (usuario.getPerfil() == Perfil.ALUNO) {
                        Aluno aluno = alunoRepository.findByUsuario(usuario);
                        if (aluno != null && aluno.getTurma() != null) {
                            dto.setTurmaId(aluno.getTurma().getId());
                        }
                    }
                    return dto;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            if (usuarioDTO.getId() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (usuarioDTO.getSenha() == null || usuarioDTO.getSenha().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Usuario usuario = new Usuario();
            usuario.setNome(usuarioDTO.getNome());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setPerfil(usuarioDTO.getPerfil());
            usuario.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));

            Usuario savedUsuario = usuarioRepository.save(usuario);

            if (savedUsuario.getPerfil() == Perfil.ALUNO && usuarioDTO.getTurmaId() != null) {
                Turma turma = turmaRepository.findById(usuarioDTO.getTurmaId()).orElse(null);
                if (turma != null) {
                    Aluno aluno = new Aluno();
                    aluno.setUsuario(savedUsuario);
                    aluno.setTurma(turma);
                    alunoRepository.save(aluno);
                }
            }

            UsuarioDTO responseDto = new UsuarioDTO(savedUsuario);
            if (savedUsuario.getPerfil() == Perfil.ALUNO) {
                Aluno aluno = alunoRepository.findByUsuario(savedUsuario);
                if (aluno != null && aluno.getTurma() != null) {
                    responseDto.setTurmaId(aluno.getTurma().getId());
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            System.err.println("Erro ao criar usuário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setNome(usuarioDTO.getNome());
                    usuarioExistente.setEmail(usuarioDTO.getEmail());
                    usuarioExistente.setPerfil(usuarioDTO.getPerfil());

                    if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
                        usuarioExistente.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
                    }

                    Usuario updatedUsuario = usuarioRepository.save(usuarioExistente);

                    if (updatedUsuario.getPerfil() == Perfil.ALUNO) {
                        Aluno aluno = alunoRepository.findByUsuario(updatedUsuario);
                        if (usuarioDTO.getTurmaId() != null) {
                            Turma turma = turmaRepository.findById(usuarioDTO.getTurmaId()).orElse(null);
                            if (turma != null) {
                                if (aluno == null) {
                                    aluno = new Aluno();
                                    aluno.setUsuario(updatedUsuario);
                                }
                                aluno.setTurma(turma);
                                alunoRepository.save(aluno);
                            }
                        } else {
                            if (aluno != null) {
                                alunoRepository.delete(aluno);
                            }
                        }
                    } else {
                        Aluno aluno = alunoRepository.findByUsuario(updatedUsuario);
                        if (aluno != null) {
                            alunoRepository.delete(aluno);
                        }
                    }

                    UsuarioDTO responseDto = new UsuarioDTO(updatedUsuario);
                    if (updatedUsuario.getPerfil() == Perfil.ALUNO) {
                        Aluno aluno = alunoRepository.findByUsuario(updatedUsuario);
                        if (aluno != null && aluno.getTurma() != null) {
                            responseDto.setTurmaId(aluno.getTurma().getId());
                        }
                    }

                    return ResponseEntity.ok(responseDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.findById(id).ifPresent(usuario -> {
                Aluno aluno = alunoRepository.findByUsuario(usuario);
                if (aluno != null) {
                    alunoRepository.delete(aluno);
                }
            });
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}