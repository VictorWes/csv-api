package com.csv.controller;


import com.csv.controller.request.AutenticacaoRequest;
import com.csv.controller.response.AutenticacaoResponse;
import com.csv.entities.Usuario;
import com.csv.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AutenticacaoResponse> login(@RequestBody @Valid AutenticacaoRequest request) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new AutenticacaoResponse(token));
    }
}
