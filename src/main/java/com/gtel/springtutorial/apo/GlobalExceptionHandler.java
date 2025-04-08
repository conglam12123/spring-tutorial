package com.gtel.springtutorial.apo;


import com.gtel.springtutorial.exception.ApplicationException;
import com.gtel.springtutorial.model.response.BadRequestResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.http.HttpResponse;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    protected final HttpServletRequest httpServletRequest;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("ERROR: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BadRequestResponse> handleApplicationException(ApplicationException ex) {
        log.info("handleApplicationException {} with message {} , title {} , data {} ", ex.getCode(), ex.getMessage(), ex.getTitle(), ex.getData());
        BadRequestResponse requestResponse = new BadRequestResponse(ex, httpServletRequest);

        String traceId = UUID.randomUUID().toString();
        requestResponse.setRequestId(traceId);

        return new ResponseEntity<>(requestResponse, HttpStatus.BAD_REQUEST);
    }


}
