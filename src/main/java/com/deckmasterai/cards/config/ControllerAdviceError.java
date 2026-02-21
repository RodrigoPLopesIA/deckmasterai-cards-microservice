package com.deckmasterai.cards.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.deckmasterai.cards.dto.ResponseErrorDTO;
import com.deckmasterai.cards.exceptions.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerAdviceError {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseErrorDTO> handleEntityNotFoundException(NotFoundException ex,  HttpServletRequest request) {
        return ResponseEntity.status(404).body(new ResponseErrorDTO(request.getRequestURI(), ex.getMessage(), 404));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        var details = ex.getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage()
                ));
        return ResponseEntity.status(400).body(new ResponseErrorDTO(request.getRequestURI(), "Validation failed", 400, details));
    }

    @ExceptionHandler(feign.FeignException.NotFound.class)
    public ResponseEntity<ResponseErrorDTO> handleFeignException(feign.FeignException.NotFound ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.status()).body(new ResponseErrorDTO(request.getRequestURI(), ex.getMessage(), ex.status()));
    }
}
