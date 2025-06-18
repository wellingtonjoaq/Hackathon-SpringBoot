package hackathon.backend.dto;

import hackathon.backend.model.Perfil;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String senha;
    private Perfil perfil;
}