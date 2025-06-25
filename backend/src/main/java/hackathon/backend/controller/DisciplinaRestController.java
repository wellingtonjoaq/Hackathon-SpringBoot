package hackathon.backend.controller;

import hackathon.backend.dto.DisciplinaDTO;
import hackathon.backend.model.Disciplina;
import hackathon.backend.model.Usuario;
import hackathon.backend.repository.UsuarioRepository;
import hackathon.backend.service.DisciplinaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/disciplinas")
public class DisciplinaRestController {

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<DisciplinaDTO>> listar(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "professor", required = false) String professorNome) {

        List<Disciplina> disciplinas = disciplinaService.buscarComFiltros(nome, professorNome);

        List<DisciplinaDTO> disciplinasDTO = disciplinas.stream()
                .map(disc -> {
                    DisciplinaDTO dto = modelMapper.map(disc, DisciplinaDTO.class);
                    if (disc.getProfessor() != null) {
                        dto.setNomeProfessor(disc.getProfessor().getNome());
                    } else {
                        dto.setNomeProfessor("Sem Professor");
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(disciplinasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaDTO> buscarPorId(@PathVariable Long id) {
        return disciplinaService.buscarPorId(id)
                .map(disciplina -> {
                    DisciplinaDTO dto = modelMapper.map(disciplina, DisciplinaDTO.class);
                    if (disciplina.getProfessor() != null) {
                        dto.setNomeProfessor(disciplina.getProfessor().getNome());
                    } else {
                        dto.setNomeProfessor("Sem Professor");
                    }
                    return dto;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DisciplinaDTO> criar(@RequestBody DisciplinaDTO disciplinaDTO) {
        try {
            if (disciplinaDTO.getId() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (disciplinaDTO.getProfessorId() == null) {
                // Professor é obrigatório para criação
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Disciplina disciplina = new Disciplina();
            disciplina.setNome(disciplinaDTO.getNome());

            Usuario professor = usuarioRepository.findById(disciplinaDTO.getProfessorId())
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado com ID: " + disciplinaDTO.getProfessorId()));
            disciplina.setProfessor(professor);

            Disciplina salvarDiciplina = disciplinaService.salvar(disciplina);

            DisciplinaDTO responseDto = modelMapper.map(salvarDiciplina, DisciplinaDTO.class);
            if (salvarDiciplina.getProfessor() != null) {
                responseDto.setNomeProfessor(salvarDiciplina.getProfessor().getNome());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            System.err.println("Erro ao criar disciplina: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisciplinaDTO> atualizar(@PathVariable Long id, @RequestBody DisciplinaDTO disciplinaDTO) {
        return disciplinaService.buscarPorId(id)
                .map(disciplinaExistente -> {
                    disciplinaExistente.setNome(disciplinaDTO.getNome());

                    if (disciplinaDTO.getProfessorId() == null) {
                        throw new IllegalArgumentException("Professor ID é obrigatório para atualização de disciplina.");
                    }

                    Usuario professor = usuarioRepository.findById(disciplinaDTO.getProfessorId())
                            .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado com ID: " + disciplinaDTO.getProfessorId()));
                    disciplinaExistente.setProfessor(professor);


                    disciplinaService.salvar(disciplinaExistente);

                    DisciplinaDTO responseDto = modelMapper.map(disciplinaExistente, DisciplinaDTO.class);
                    if (disciplinaExistente.getProfessor() != null) {
                        responseDto.setNomeProfessor(disciplinaExistente.getProfessor().getNome());
                    }

                    return ResponseEntity.ok(responseDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (disciplinaService.buscarPorId(id).isPresent()) {
            disciplinaService.deletarPorId(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
