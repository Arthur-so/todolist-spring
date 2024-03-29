package br.com.arthuroliveira.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import br.com.arthuroliveira.todolist.user.IUserRepository;
import br.com.arthuroliveira.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                String servletPath = request.getServletPath();

                if (servletPath.startsWith("/tasks/")) {

        
                    // Pegar a autenticação (usuário e senha)
                    String authorization = request.getHeader("Authorization");
                    String authEncoded =  authorization.substring("Basic".length()).trim();
                    byte[] authDecoded =  Base64.getDecoder().decode(authEncoded);
                    String authString = new String(authDecoded);

                    String[] credentials = authString.split(":");
                    String username = credentials[0];
                    String password = credentials[1];

                    // Validar usuário
                    UserModel user = this.userRepository.findByUsername(username);
                    if (user == null) {
                        response.sendError(401);
                    } 
                    else {
                        // Validar senha
                        Result passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                        if (passwordVerify.verified) {
                            //Segue viagem
                            request.setAttribute("idUser", user.getId());
                            filterChain.doFilter(request, response);
                        } 
                        else {
                            response.sendError(401);
                        }
                    }
                } else {
                    filterChain.doFilter(request, response);
                }
               
    }
    
}
