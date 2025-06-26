package hackathon.backend.controller;

import hackathon.backend.dto.NotaAlunoDetalheDTO;
import hackathon.backend.model.Perfil;
import hackathon.backend.model.Usuario;
import hackathon.backend.service.NotaAlunoService;
import hackathon.backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aluno/notas")
public class NotaAlunoRestController {

    @Autowired
    private NotaAlunoService notaAlunoService;

    @GetMapping("/{alunoId}")
    public List<NotaAlunoDetalheDTO> listarNotasDoAluno(@PathVariable Long alunoId) {
        return notaAlunoService.listarNotasDoAluno(alunoId);
    }
}
