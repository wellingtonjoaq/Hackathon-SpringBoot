package hackathon.backend.service;

import hackathon.backend.dto.RespostaAlunoDTO;
import hackathon.backend.dto.RespostaAlunoDetalheDTO;
import hackathon.backend.model.Perfil;
import hackathon.backend.model.Prova;
import hackathon.backend.model.ProvaGabarito;
import hackathon.backend.model.RespostaAluno;
import hackathon.backend.model.RespostaAlunoDetalhe;
import hackathon.backend.model.Usuario;
import hackathon.backend.repository.ProvaRepository;
import hackathon.backend.repository.RespostaAlunoRepository;
import hackathon.backend.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RespostaAlunoService {

    @Autowired
    private RespostaAlunoRepository repository;

    @Autowired
    private ProvaRepository provaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsuarioService usuarioService;


    @Transactional
    public void salvar(RespostaAlunoDTO dto) {
        Usuario aluno = usuarioRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + dto.getAlunoId()));

        Prova prova = provaRepository.findById(dto.getProvaId())
                .orElseThrow(() -> new RuntimeException("Prova não encontrada com ID: " + dto.getProvaId()));

        RespostaAluno respostaAluno;
        if (dto.getId() != null) {
            respostaAluno = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Resposta do aluno não encontrada"));
            respostaAluno.getDetalhes().clear();
        } else {
            respostaAluno = new RespostaAluno();
        }

        respostaAluno.setAluno(aluno);
        respostaAluno.setProva(prova);
        respostaAluno.setDetalhes(new ArrayList<>());

        double notaCalculada = 0.0;
        if (dto.getDetalhes() != null && !dto.getDetalhes().isEmpty()) {
            Map<Integer, String> gabaritoMap = prova.getGabarito().stream()
                    .collect(Collectors.toMap(ProvaGabarito::getNumeroQuestao, ProvaGabarito::getRespostaCorreta));

            int questoesCorretas = 0;
            for (RespostaAlunoDetalheDTO detalheDTO : dto.getDetalhes()) {
                RespostaAlunoDetalhe detalhe = new RespostaAlunoDetalhe();
                detalhe.setNumeroQuestao(detalheDTO.getNumeroQuestao());
                detalhe.setResposta(detalheDTO.getResposta());
                detalhe.setRespostaAluno(respostaAluno);

                String respostaCorreta = gabaritoMap.get(detalheDTO.getNumeroQuestao());
                if (respostaCorreta != null && detalheDTO.getResposta() != null &&
                        detalheDTO.getResposta().trim().equalsIgnoreCase(respostaCorreta.trim())) {
                    questoesCorretas++;
                }
                respostaAluno.getDetalhes().add(detalhe);
            }
            notaCalculada = (double) questoesCorretas / prova.getGabarito().size() * 10.0;
        }
        respostaAluno.setNota(notaCalculada);
        repository.save(respostaAluno);
    }

    public RespostaAlunoDTO buscarPorId(Long id) {
        RespostaAluno respostaAluno = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resposta não encontrada com ID: " + id));

        RespostaAlunoDTO dto = modelMapper.map(respostaAluno, RespostaAlunoDTO.class);

        if (dto.getDetalhes() == null) {
            dto.setDetalhes(new ArrayList<>());
        }

        Prova prova = respostaAluno.getProva();

        Map<Integer, String> gabaritoMap = prova.getGabarito().stream()
                .collect(Collectors.toMap(ProvaGabarito::getNumeroQuestao, ProvaGabarito::getRespostaCorreta));

        for (RespostaAlunoDetalheDTO detalheDto : dto.getDetalhes()) {
            String respostaCorreta = gabaritoMap.get(detalheDto.getNumeroQuestao());
            detalheDto.setRespostaCorreta(respostaCorreta);

            if (respostaCorreta != null) {
                boolean isCorreta = detalheDto.getResposta() != null &&
                        detalheDto.getResposta().trim().equalsIgnoreCase(respostaCorreta.trim());
                detalheDto.setCorreta(isCorreta);
            } else {
                detalheDto.setCorreta(false);
            }
        }

        return dto;
    }

    @Transactional
    public void deletarPorId(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Resposta do aluno não encontrada com o ID: " + id);
        }
        repository.deleteById(id);
    }

    public List<RespostaAluno> listarTodos() {
        return repository.findAll();
    }

    public List<RespostaAluno> listarPorFiltros(Long turmaId, Long disciplinaId, LocalDate dataProva) {
        Long professorId = null;
        if (usuarioService.getUsuarioLogado() != null && usuarioService.getUsuarioLogado().getPerfil() == Perfil.PROFESSOR) {
            professorId = usuarioService.getUsuarioLogado().getId();
        }

        if (professorId != null) {
            return repository.findByProfessorIdAndFilters(professorId, turmaId, disciplinaId, dataProva);
        } else {
            List<RespostaAluno> respostas = repository.findAll();

            return respostas.stream()
                    .filter(ra -> {
                        boolean passaTurma = (turmaId == null || (ra.getProva().getTurma() != null && ra.getProva().getTurma().getId().equals(turmaId)));
                        boolean passaDisciplina = (disciplinaId == null || (ra.getProva().getDisciplina() != null && ra.getProva().getDisciplina().getId().equals(disciplinaId)));
                        boolean passaData = (dataProva == null || (ra.getProva().getData() != null && ra.getProva().getData().isEqual(dataProva)));

                        return passaTurma && passaDisciplina && passaData;
                    })
                    .collect(Collectors.toList());
        }
    }
}