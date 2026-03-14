package com.csv.infra.security;

import com.csv.enums.PerfilEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    @Autowired
    private TratarErrosAutenticacao tratadorDeErrosAutenticacao;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        String[] rotasPostAdmin = {"/usuarios", "/empresas"};

        String[] rotasPostGerencia = {"/produtos", "/formas-pagamento", "/lancamentos"};

        String[] rotasPostOperacao = {"/clientes", "/vendas"};

        String[] rotasPatchOperacao = {"/clientes/{id}", "/vendas/{id}"};

        String[] rotasDeleteGerencia = {
                "/clientes/{id}", "/empresas/{id}", "/usuarios/{id}",
                "/produtos/{id}", "/formas-pagamento/{id}", "/lancamentos/{id}", "/vendas/{id}"
        };

        String[] rotasPatchGerencia = {
                "/empresas/{id}", "/usuarios/{id}", "/formas-pagamento/{id}", "/lancamentos/{id}"
        };

        String[] rotasGetGerencia = {
                "/formas-pagamento/inativas", "/lancamentos/conta/{contaId}", "/lancamentos/{id}", "/vendas/empresa/{empresaId}"
        };

        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Regras de ADMIN (Acesso Exclusivo)
                        .requestMatchers(HttpMethod.POST, rotasPostAdmin).hasAuthority(PerfilEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/clientes/inativos").hasAuthority(PerfilEnum.ADMIN.name())

                        // 3. Regras de GERÊNCIA (Admin e Gerente)
                        .requestMatchers(HttpMethod.POST, rotasPostGerencia).hasAnyAuthority(PerfilEnum.ADMIN.name(), PerfilEnum.GERENTE.name())
                        .requestMatchers(HttpMethod.DELETE, rotasDeleteGerencia).hasAnyAuthority(PerfilEnum.ADMIN.name(), PerfilEnum.GERENTE.name())
                        .requestMatchers(HttpMethod.PATCH, rotasPatchGerencia).hasAnyAuthority(PerfilEnum.ADMIN.name(), PerfilEnum.GERENTE.name())
                        .requestMatchers(HttpMethod.GET, rotasGetGerencia).hasAnyAuthority(PerfilEnum.ADMIN.name(), PerfilEnum.GERENTE.name())

                        // 4. Regras de OPERAÇÃO (Admin, Gerente e Operador)
                        .requestMatchers(HttpMethod.POST, rotasPostOperacao).hasAnyAuthority(PerfilEnum.ADMIN.name(), PerfilEnum.GERENTE.name(), PerfilEnum.OPERADOR.name())
                        .requestMatchers(HttpMethod.PATCH, rotasPatchOperacao).hasAnyAuthority(PerfilEnum.ADMIN.name(), PerfilEnum.GERENTE.name(), PerfilEnum.OPERADOR.name())
                        .requestMatchers(HttpMethod.GET, "/vendas/{id}").hasAnyAuthority(PerfilEnum.ADMIN.name(), PerfilEnum.GERENTE.name(), PerfilEnum.OPERADOR.name())

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(tratadorDeErrosAutenticacao))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}