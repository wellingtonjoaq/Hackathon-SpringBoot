package hackathon.backend.service;

import hackathon.backend.dto.RespostaAlunoDTO;
import hackathon.backend.dto.RespostaAlunoDetalheDTO;
import hackathon.backend.model.*;
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

    @Transactional
    public void salvar(RespostaAlunoDTO dto) {
        Usuario aluno = usuarioRepository.findById(dto.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + dto.getAlunoId()));

        Prova prova = provaRepository.findById(dto.getProvaId())
                .orElseThrow(() -> new RuntimeException("Prova não encontrada com ID: " + dto.getProvaId()));

        Map<Integer, String> gabaritoMap = prova.getGabarito().stream()
                .collect(Collectors.toMap(ProvaGabarito::getNumeroQuestao, ProvaGabarito::getRespostaCorreta));

        RespostaAluno respostaAluno;
        if (dto.getId() != null) {
            respostaAluno = repository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Resposta não encontrada com ID: " + dto.getId()));
            respostaAluno.getDetalhes().clear();
        } else {
            respostaAluno = new RespostaAluno();
            respostaAluno.setDetalhes(new ArrayList<>());
        }

        respostaAluno.setAluno(aluno);
        respostaAluno.setProva(prova);

        int acertos = 0;
        List<RespostaAlunoDetalhe> detalhes = new ArrayList<>();

        for (RespostaAlunoDetalheDTO detalheDTO : dto.getDetalhes()) {
            String respostaCorreta = gabaritoMap.get(detalheDTO.getNumeroQuestao());

            boolean correta = respostaCorreta != null &&
                    detalheDTO.getResposta() != null &&
                    detalheDTO.getResposta().trim().equalsIgnoreCase(respostaCorreta.trim());

            if (correta) acertos++;

            RespostaAlunoDetalhe detalhe = new RespostaAlunoDetalhe();
            detalhe.setNumeroQuestao(detalheDTO.getNumeroQuestao());
            detalhe.setResposta(detalheDTO.getResposta());
            detalhe.setRespostaAluno(respostaAluno);

            detalhes.add(detalhe);
        }

        respostaAluno.setDetalhes(detalhes);

        int totalQuestoes = prova.getGabarito().size();
        double nota = totalQuestoes > 0 ? (acertos / (double) totalQuestoes) * 10.0 : 0.0;
        respostaAluno.setNota(nota);

        repository.save(respostaAluno);
    }

    public List<RespostaAluno> listarTodos() {
        return repository.findAll();
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
            throw new RuntimeException("Resposta não encontrada com ID: " + id);
        }
        repository.deleteById(id);
    }
}
