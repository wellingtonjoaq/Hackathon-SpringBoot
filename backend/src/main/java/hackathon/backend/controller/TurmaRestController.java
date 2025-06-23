package hackathon.backend.controller;

import hackathon.backend.dto.TurmaDTO;
import hackathon.backend.model.Turma;
import hackathon.backend.service.TurmaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/turmas") // importante para o Flutter
@CrossOrigin(origins = "*") // permite chamadas do Flutter
public class TurmaRestController {

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<TurmaDTO> listarTurmas() {
        List<Turma> turmas = turmaService.listarTodos();
        return turmas.stream()
                .map(t -> modelMapper.map(t, TurmaDTO.class))
                .collect(Collectors.toList());
    }
}
