package hackathon.backend.controller;

import hackathon.backend.model.Usuario;
import hackathon.backend.model.Perfil;
import hackathon.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Página para cadastro (formulário em branco ou com objeto já preenchido)
    @GetMapping()
    public String iniciar(Usuario usuario, Model model) {
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        return "usuario/formulario";
    }

    // Salvar novo usuário ou editar existente
    @PostMapping("salvar")
    public String salvar(Usuario usuario, Model model) {
        try {
            // Só criptografa a senha se for nova ou diferente
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            } else if (usuario.getId() != null) {
                // Caso a senha não seja enviada no form ao editar, manter a senha antiga
                Usuario usuarioExistente = usuarioRepository.findById(usuario.getId()).orElse(null);
                if (usuarioExistente != null) {
                    usuario.setSenha(usuarioExistente.getSenha());
                }
            }

            usuarioRepository.save(usuario);
            return "redirect:/usuario/listar";

        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar usuário");
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("perfis", Perfil.values());
            return "usuario/formulario";
        }
    }

    // Lista todos usuários
    @GetMapping("listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuario/lista";
    }

    // Editar usuário por id - abre formulário preenchido
    @GetMapping("editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            // Pode redirecionar para a lista caso usuário não exista
            return "redirect:/usuario/listar";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("perfis", Perfil.values());
        return "usuario/formulario";
    }

    // Remover usuário por id
    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuario/listar";
    }
}
