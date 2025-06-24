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
            notaCalculada = (double) questoesCorretas / prova.getGabarito().size() * 10.0; // Assume nota de 0 a 10
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

    public List<RespostaAluno> listarPorFiltros(Long alunoId, Long provaId) {
        // Obtém o usuário logado
        Long professorId = null;
        if (usuarioService.getUsuarioLogado() != null && usuarioService.getUsuarioLogado().getPerfil() == Perfil.PROFESSOR) {
            professorId = usuarioService.getUsuarioLogado().getId();
        }

        if (professorId != null) {
            return repository.findByProfessorIdAndFilters(professorId, alunoId, provaId);
        } else {
            if (alunoId != null && provaId != null) {
                return repository.findAll().stream()
                        .filter(ra -> ra.getAluno().getId().equals(alunoId) && ra.getProva().getId().equals(provaId))
                        .collect(Collectors.toList());
            } else if (alunoId != null) {
                return repository.findAll().stream()
                        .filter(ra -> ra.getAluno().getId().equals(alunoId))
                        .collect(Collectors.toList());
            } else if (provaId != null) {
                return repository.findAll().stream()
                        .filter(ra -> ra.getProva().getId().equals(provaId))
                        .collect(Collectors.toList());
            } else {
                return repository.findAll();
            }
        }
    }
}