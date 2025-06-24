package hackathon.backend.security;

import hackathon.backend.model.Perfil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"))) {
            response.sendRedirect("/usuario/listar");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR"))) {
            response.sendRedirect("/prova/listar");
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ALUNO"))) {
            response.sendRedirect("/aluno/notas");
        }
    }
}
