package hackathon.backend.service;

import hackathon.backend.model.Turma;
import hackathon.backend.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurmaService {
    @Transactional
    public Turma salvar(Turma turma) { // Deve retornar Turma
        return repository.save(turma);
    }

    @Autowired
    private TurmaRepository repository;


    public List<Turma> listarTodos() {
        return repository.findAll();
    }

    public Turma buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public void deletarPorId(Long id) {
        repository.deleteById(id);
    }

    public List<Turma> buscarComFiltros(String nome, String periodo, String curso) {
        String nomeFiltro = nome != null ? nome : "";
        String periodoFiltro = periodo != null ? periodo : "";
        String cursoFiltro = curso != null ? curso : "";
        return repository.findByNomeContainingIgnoreCaseAndPeriodoContainingIgnoreCaseAndCursoContainingIgnoreCase(
                nomeFiltro,
                periodoFiltro,
                cursoFiltro
        );
    }

    public List<String> buscarPeriodos() {
        return repository.findDistinctPeriodo();
    }

    public List<String> buscarCursos() {
        return repository.findDistinctCurso();
    }
}