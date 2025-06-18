package hackathon.backend.controller;

import hackathon.backend.model.Usuario;
import hackathon.backend.model.Perfil;
import hackathon.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping()
    public String iniciar(Usuario usuario, Model model) {
        model.addAttribute("perfis", Perfil.values());
        return "usuario/formulario";
    }

    @PostMapping("salvar")
    public String salvar(Usuario usuario, Model model) {
        try {
            usuarioRepository.save(usuario);
            return "redirect:/usuario/listar";
        } catch (Exception e) {
            model.addAttribute("errotitulo", "Erro ao salvar usuário");
            model.addAttribute("erro", e.getMessage());
            return iniciar(usuario, model);
        }
    }

    @GetMapping("listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuario/lista";
    }

    @GetMapping("editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioRepository.findById(id).orElse(null));
        model.addAttribute("perfis", Perfil.values());
        return "usuario/formulario";
    }

    @GetMapping("remover/{id}")
    public String remover(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuario/listar";
    }
}
