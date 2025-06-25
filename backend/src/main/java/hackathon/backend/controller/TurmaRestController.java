package hackathon.backend.controller;

import hackathon.backend.dto.TurmaDTO;
import hackathon.backend.model.Turma;
import hackathon.backend.service.TurmaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
public class TurmaRestController {

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private ModelMapper modelMapper;

    // GET /api/turmas
    @GetMapping
    public List<TurmaDTO> listarTurmas(
            @RequestParam(required = false) String filtroNome,
            @RequestParam(required = false) String filtroPeriodo,
            @RequestParam(required = false) String filtroCurso
    ) {
        List<Turma> turmas = turmaService.buscarComFiltros(filtroNome, filtroPeriodo, filtroCurso);
        return turmas.stream()
                .map(t -> modelMapper.map(t, TurmaDTO.class))
                .collect(Collectors.toList());
    }

    // GET /api/turmas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TurmaDTO> buscarPorId(@PathVariable Long id) {
        try {
            Turma turma = turmaService.buscarPorId(id);
            return ResponseEntity.ok(modelMapper.map(turma, TurmaDTO.class));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/turmas
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody TurmaDTO turmaDTO) {
        try {
            Turma turma = modelMapper.map(turmaDTO, Turma.class);
            Turma salva = turmaService.salvar(turma);
            return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(salva, TurmaDTO.class));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar turma: " + e.getMessage());
        }
    }

    // PUT /api/turmas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody TurmaDTO turmaDTO) {
        try {
            Turma turma = modelMapper.map(turmaDTO, Turma.class);
            turma.setId(id);
            Turma atualizada = turmaService.salvar(turma);
            return ResponseEntity.ok(modelMapper.map(atualizada, TurmaDTO.class));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar turma: " + e.getMessage());
        }
    }

    // DELETE /api/turmas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remover(@PathVariable Long id) {
        try {
            turmaService.deletarPorId(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível excluir a turma pois há dados vinculados a ela.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir a turma: " + e.getMessage());
        }
    }

    // GET /api/turmas/periodos
    @GetMapping("/periodos")
    public List<String> listarPeriodos() {
        return turmaService.buscarPeriodos();
    }

    // GET /api/turmas/cursos
    @GetMapping("/cursos")
    public List<String> listarCursos() {
        return turmaService.buscarCursos();
    }
}
