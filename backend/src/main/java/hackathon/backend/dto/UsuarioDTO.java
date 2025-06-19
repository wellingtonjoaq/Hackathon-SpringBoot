package hackathon.backend.dto;

import hackathon.backend.model.Perfil;
import hackathon.backend.model.Usuario;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String senha; // pode deixar ou omitir aqui
    private Perfil perfil;

    public UsuarioDTO() {}

    // Construtor para converter Usuario em DTO (sem senha)
    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.perfil = usuario.getPerfil();
        // NÃO copia a senha para não expor
    }
}
