package hackathon.backend.controller;

import hackathon.backend.dto.DisciplinaDTO;
import hackathon.backend.service.DisciplinaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/disciplinas")
@CrossOrigin(origins = "*")
public class DisciplinaRestController {

    @Autowired
    private DisciplinaService disciplinaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<DisciplinaDTO> listarDisciplinas() {
        return disciplinaService.listarTodos()
                .stream()
                .map(d -> modelMapper.map(d, DisciplinaDTO.class))
                .collect(Collectors.toList());
    }
}
