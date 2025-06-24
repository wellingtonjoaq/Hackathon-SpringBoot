package hackathon.backend.service;

import hackathon.backend.model.Disciplina;
import hackathon.backend.repository.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository repository;

    @Transactional
    public void salvar(Disciplina disciplina) {
        repository.save(disciplina);
    }

    public List<Disciplina> listarTodos() {
        return repository.findAll();
    }

    public Optional<Disciplina> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletarPorId(Long id) {
        repository.deleteById(id);
    }

    public List<Disciplina> buscarComFiltros(String nome, String professor) {
        if (nome != null && nome.trim().isEmpty()) {
            nome = null;
        }
        if (professor != null && professor.trim().isEmpty()) {
            professor = null;
        }
        return repository.buscarComFiltros(nome, professor);
    }

    public List<String> listarProfessoresUnicos() {
        return repository.listarProfessoresUnicos();
    }
}
