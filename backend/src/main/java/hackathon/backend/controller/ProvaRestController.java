package hackathon.backend.controller;

import hackathon.backend.dto.ProvaDTO;
import hackathon.backend.service.ProvaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/provas")
@CrossOrigin(origins = "*")
public class ProvaRestController {

    @Autowired
    private ProvaService provaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<ProvaDTO> listarProvas() {
        return provaService.listarTodos()
                .stream()
                .map(prova -> modelMapper.map(prova, ProvaDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvaDTO> buscarProva(@PathVariable Long id) {
        try {
            var prova = provaService.buscarPorId(id);
            var dto = modelMapper.map(prova, ProvaDTO.class);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarProva(@RequestBody ProvaDTO provaDTO) {
        try {
            var prova = modelMapper.map(provaDTO, hackathon.backend.model.Prova.class);
            provaService.salvar(prova);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerProva(@PathVariable Long id) {
        try {
            provaService.deletarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
