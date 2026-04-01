package com.floriano.legato_api.exceptions;

import com.floriano.legato_api.payload.ApiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ApiResponse<>(false, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalState(IllegalStateException ex) {
        return new ResponseEntity<>(new ApiResponse<>(false, ex.getMessage(), null), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException() {
        return ResponseEntity.badRequest().body("Arquivo excede o limite permitido.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Email ou senha incorretos.", null));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        logger.warn("Falha de autenticacao: {}", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Email ou senha incorretos.", null));
    }

// Trata erros do Hibernate/Banco de Dados (ex: violação de constraint, índice nulo)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        logger.error("Erro de integridade de dados", ex);
        return new ResponseEntity<>(new ApiResponse<>(false, "Erro no banco de dados: " + ex.getMostSpecificCause().getMessage(), null), HttpStatus.CONFLICT);
    }

    // Trata erros de validação de DTOs (@Valid, @NotNull, etc)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (partialString, element) -> partialString + element + "; ");
        return new ResponseEntity<>(new ApiResponse<>(false, "Erro de validação: " + errorMessage, null), HttpStatus.BAD_REQUEST);
    }

    // O "Pega-Tudo" agora vai te "dedurar" o erro real para o front-end
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(Exception ex) {
        logger.error("Erro interno no servidor", ex);
        // Expondo a mensagem real da exception no lugar da mensagem genérica
        return new ResponseEntity<>(
                new ApiResponse<>(false, "Erro no servidor: " + ex.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<ApiResponse<Object>> forbiddenActionException(ForbiddenActionException ex) {
        return new ResponseEntity<>(new ApiResponse<>(false, ex.getMessage(), null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> postNotFoundException(PostNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }
}
