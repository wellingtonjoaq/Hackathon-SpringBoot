package hackathon.backend.service;

import hackathon.backend.model.Prova;
import hackathon.backend.repository.ProvaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProvaService {

    @Autowired
    private ProvaRepository repository;

    @Transactional
    public void salvar(Prova prova) {
        repository.save(prova);
    }

    public List<Prova> listarTodos() {
        return repository.findAll();
    }

    public Prova buscarPorId(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public void deletarPorId(Long id) {
        repository.deleteById(id);
    }
}
