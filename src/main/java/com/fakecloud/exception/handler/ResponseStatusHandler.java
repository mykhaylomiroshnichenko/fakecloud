package com.fakecloud.exception.handler;

import com.fakecloud.exception.BadRequestException;
import com.fakecloud.exception.PreconditionFailedException;
import com.fakecloud.security.jwt.JwtAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class ResponseStatusHandler {

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<String> badRequestExceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundExceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = PreconditionFailedException.class)
    public ResponseEntity<String> preconditionFailedExceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(value = JwtAuthenticationException.class)
    public ResponseEntity<String> jwtAuthenticationExceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
