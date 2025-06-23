package hackathon.backend.service;

import hackathon.backend.model.Turma;
import hackathon.backend.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    @Transactional
    public void salvar(Turma turma) {
        repository.save(turma);
    }

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
        return repository.findByNomeContainingIgnoreCaseAndPeriodoContainingIgnoreCaseAndCursoContainingIgnoreCase(
                nome != null ? nome : "",
                periodo != null ? periodo : "",
                curso != null ? curso : ""
        );
    }

    public List<String> buscarPeriodos() {
        return repository.findDistinctPeriodo();
    }

    public List<String> buscarCursos() {
        return repository.findDistinctCurso();
    }
}

