package ru.gb.cafeteria.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.gb.cafeteria.security.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

	private AuthHandler authHandler;

	public CustomUserDetailsService userDetailsService;

	@Bean
	public AuthenticationProvider authenticationProvider(){
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/css/**", "/favicon.ico", "/img/**", "/", "/login", "/logout").permitAll()
                                .requestMatchers("/admin").hasAnyRole("ADMIN")
                                .requestMatchers("/manager").hasAnyRole("MANAGER")
                                .requestMatchers("/kitchen").hasAnyRole("CHEF")
                                .requestMatchers("/cashier").hasAnyRole("CASHIER")
                                .requestMatchers("/client").hasAnyRole("CLIENT")
                                .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .successHandler(authHandler)
                        .permitAll())
                .logout(logout ->
						logout
                        	.logoutSuccessUrl("/")
								.permitAll());

        return http.build();
    }

    // БИН кодировщика паролей
	// Ставим степень кодировки, с которой кодировали пароль в базе - 5
	@Bean
	public PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(5);
	}

}