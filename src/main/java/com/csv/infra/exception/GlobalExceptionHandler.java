package com.csv.infra.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {

        var erros = ex.getFieldErrors();
        var payload = erros.stream().map(DadosErroValidacao::new).toList();

        return ResponseEntity.badRequest().body(payload);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<DadosErroPadrao> tratarRegraDeNegocio(RegraNegocioException ex) {
        return ResponseEntity.badRequest().body(new DadosErroPadrao(ex.getMessage()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<DadosErroPadrao> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErroPadrao(ex.getMessage()));
    }
}
