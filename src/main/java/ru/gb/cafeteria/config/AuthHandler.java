package ru.gb.cafeteria.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

// класс-обработчик аутентификации
// в зависимости от роли отправляет на соответствующую страницу
//

@Configuration
public class AuthHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        roles.forEach(System.out::println);

        if (roles.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin");
        }
        if (roles.contains("ROLE_MANAGER")) {
            response.sendRedirect("/management");
        }
        if (roles.contains("ROLE_CHEF")) {
            response.sendRedirect("/kitchen");
        }
        if (roles.contains("ROLE_CASHIER")) {
            response.sendRedirect("/menu");
        }
    }
}
