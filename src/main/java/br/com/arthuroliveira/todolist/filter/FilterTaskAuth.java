package br.com.arthuroliveira.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
                // Pegar a autenticação (usuário e senha)
                String authorization = request.getHeader("Authorization");
                String authEncoded =  authorization.substring("Basic".length()).trim();
                byte[] authDecoded =  Base64.getDecoder().decode(authEncoded);
                String authString = new String(authDecoded);

                String[] credentials = authString.split(":");
                String username = credentials[0];
                String password = credentials[1];

                System.out.println(username);
                System.out.println(password);

                filterChain.doFilter(request, response);
    }
    
}