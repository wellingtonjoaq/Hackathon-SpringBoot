package hackathon.backend.service;

import hackathon.backend.dto.GabaritoDTO;
import hackathon.backend.dto.ProvaDTO;
import hackathon.backend.model.*;
import hackathon.backend.repository.DisciplinaRepository;
import hackathon.backend.repository.ProvaRepository;
import hackathon.backend.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class ProvaService {

    private static final Logger logger = LoggerFactory.getLogger(ProvaService.class);

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public void salvarProvaComGabarito(ProvaDTO dto) {
        logger.debug("DTO.getId() on entry: {}", dto.getId());

        Turma turma = turmaRepository.findById(dto.getTurmaId())
                .orElseThrow(() -> new RuntimeException("Turma não encontrada com ID: " + dto.getTurmaId()));
        Disciplina disciplina = disciplinaRepository.findById(dto.getDisciplinaId())
                .orElseThrow(() -> new RuntimeException("Disciplina não encontrada com ID: " + dto.getDisciplinaId()));

        List<Prova> provasExistentes;
        if (dto.getId() == null) {
            provasExistentes = buscarPorDisciplinaTurmaEBimestre(disciplina.getId(), turma.getId(), Bimestre.valueOf(dto.getBimestre()));
        } else {
            provasExistentes = buscarPorDisciplinaTurmaEBimestreExcetoId(disciplina.getId(), turma.getId(), Bimestre.valueOf(dto.getBimestre()), dto.getId());
        }

        if (!provasExistentes.isEmpty()) {
            throw new IllegalArgumentException("Já existe uma prova cadastrada para esta disciplina e bimestre nesta turma.");
        }

        Prova prova;
        if (dto.getId() != null) {
            prova = provaRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Prova não encontrada para edição com ID: " + dto.getId()));
            logger.debug("Prova fetched from DB. ID: {}", prova.getId());
        } else {
            prova = new Prova();
            logger.debug("Creating new Prova instance (ID will be null here for new record).");
        }

        prova.setTitulo(dto.getTitulo());
        prova.setData(dto.getData());
        prova.setTurma(turma);
        prova.setDisciplina(disciplina);
        prova.setBimestre(Bimestre.valueOf(dto.getBimestre()));

        if (prova.getGabarito() != null) {
            prova.getGabarito().clear();
        } else {
            // If the collection is null (shouldn't happen with proper entity init), initialize it.
            prova.setGabarito(new ArrayList<>()); // Change to new HashSet<>() if using Set
        }

        if (dto.getGabarito() != null) {
            // Create new ProvaGabarito instances from DTOs and link them to the Prova
            for (GabaritoDTO gabaritoDTO : dto.getGabarito()) {
                ProvaGabarito provaGabarito = new ProvaGabarito();
                provaGabarito.setNumeroQuestao(gabaritoDTO.getNumeroQuestao());
                provaGabarito.setRespostaCorreta(gabaritoDTO.getRespostaCorreta());
                provaGabarito.setProva(prova); // IMPORTANT: Set the bidirectional relationship
                prova.getGabarito().add(provaGabarito);
            }
        }
        // --- END OF FIX ---

        logger.debug("Prova object BEFORE save. ID: {}, Title: {}", prova.getId(), prova.getTitulo());
        provaRepository.save(prova); // This save operation will now manage gabaritos correctly
        logger.debug("Prova object AFTER save. Final ID: {}", prova.getId());
    }


    @Transactional
    public void salvar(Prova prova) {
        provaRepository.save(prova);
    }

    public Prova buscarPorId(Long id) {
        return provaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prova não encontrada com ID: " + id));
    }

    public List<Prova> listarTodos() {
        return provaRepository.findAll();
    }

    public List<Prova> buscarPorFiltros(Long turmaId, Long disciplinaId, String bimestreString, LocalDate data) {
        Bimestre bimestreEnum = null;
        if (bimestreString != null && !bimestreString.trim().isEmpty()) {
            try {
                bimestreEnum = Bimestre.valueOf(bimestreString.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.println("Bimestre inválido recebido: " + bimestreString);
            }
        }

        Long professorId = null;
        if (usuarioService.getUsuarioLogado() != null && usuarioService.getUsuarioLogado().getPerfil() == Perfil.PROFESSOR) {
            professorId = usuarioService.getUsuarioLogado().getId();
        }

        if (professorId != null) {
            return provaRepository.buscarProvasDoProfessor(professorId, turmaId, disciplinaId, bimestreEnum, data);
        } else {
            return provaRepository.buscarPorFiltros(turmaId, disciplinaId, bimestreEnum, data);
        }
    }

    public List<Prova> buscarPorDisciplinaTurmaEBimestre(Long disciplinaId, Long turmaId, Bimestre bimestre) {
        return provaRepository.findByDisciplinaIdAndTurmaIdAndBimestre(disciplinaId, turmaId, bimestre);
    }

    public List<Prova> buscarPorDisciplinaTurmaEBimestreExcetoId(Long disciplinaId, Long turmaId, Bimestre bimestre, Long provaIdExcluir) {
        return provaRepository.findByDisciplinaIdAndTurmaIdAndBimestreAndIdIsNot(disciplinaId, turmaId, bimestre, provaIdExcluir);
    }

    public List<Prova> buscarPorDisciplinaEBimestre(Long disciplinaId, Bimestre bimestre) {
        return provaRepository.findByDisciplinaIdAndBimestre(disciplinaId, bimestre);
    }

    public List<Prova> buscarPorDisciplinaEBimestreExcetoId(Long disciplinaId, Bimestre bimestre, Long provaIdExcluir) {
        return provaRepository.findByDisciplinaIdAndBimestreAndIdIsNot(disciplinaId, bimestre, provaIdExcluir);
    }

    @Transactional
    public void deletarPorId(Long id) {
        provaRepository.deleteById(id);
    }
}