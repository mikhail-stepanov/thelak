package com.thelak.core.endpoints;

import com.thelak.route.common.models.ErrorResponse;
import com.thelak.route.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

@ControllerAdvice
public abstract class MicroserviceAdvice {

    protected static final Logger log = LoggerFactory.getLogger(MicroserviceAdvice.class);

    @Value("${spring.application.name}")
    protected String applicationName;

    @ExceptionHandler(MsObjectNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleMsObjectNotFoundException(MsObjectNotFoundException ex, WebRequest request) {
        return handleMicroserviceException(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MsBadRequestException.class)
    public final ResponseEntity<ErrorResponse> handleMsBadRequestException(MsBadRequestException ex, WebRequest request) {
        return handleMicroserviceException(ex, request, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MsAlreadyExistsException.class)
    public final ResponseEntity<ErrorResponse> handleMsBadRequestException(MsAlreadyExistsException ex, WebRequest request) {
        return handleMicroserviceException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MsNotAuthorizedException.class)
    public final ResponseEntity<ErrorResponse> handleMsNotAuthorizedException(MsNotAuthorizedException ex, WebRequest request) {
        return handleMicroserviceException(ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MsNotAllowedException.class)
    public final ResponseEntity<ErrorResponse> handleMsNotAllowedException(MsNotAllowedException ex, WebRequest request) {
        return handleMicroserviceException(ex, request, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MsInternalErrorException.class)
    public final ResponseEntity<ErrorResponse> handleMsInternalErrorException(MsInternalErrorException ex, WebRequest request) {
        return handleMicroserviceException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuffer fields = new StringBuffer();
        for (FieldError field : fieldErrors) {
            fields.append(field.getField() + ", ");
        }
        fields.delete(fields.length() - 2, fields.length());
        return handleMicroserviceException(new MsBadRequestException(fields.toString()), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleMsInternalErrorException(Exception ex, WebRequest request) {
        return handleMicroserviceException(new MsInternalErrorException(ex.getMessage()), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getInternalSessionId() {
        return header("x-thelak-sid")
                .orElse(UUID.randomUUID().toString());
    }

    private String getRequestURL(HttpServletRequest httpRequest) {
        return httpRequest.getRequestURL().toString();
    }

    private String getRequestBody(HttpServletRequest httpRequest) {
        String body = null;
        if (httpRequest.getMethod().equalsIgnoreCase("POST") || httpRequest.getMethod().equalsIgnoreCase("PUT")) {
            Scanner s;
            try {
                s = new Scanner(httpRequest.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException ex) {
                return ex.getMessage();
            }
            body = s.hasNext() ? s.next() : "";
        }
        return body;
    }

    protected Optional<String> header(String headerName) {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(attrs -> (ServletRequestAttributes) attrs)
                .map(ServletRequestAttributes::getRequest)
                .map(request -> request.getHeader(headerName));
    }

    @ResponseBody
    private ResponseEntity<ErrorResponse> handleMicroserviceException(MicroServiceException ex, WebRequest request, HttpStatus httpStatus) {
        HttpServletRequest httpRequest = ((ServletWebRequest) request).getRequest();

        String requestBody = getRequestBody(httpRequest);
        String requestURL = getRequestURL(httpRequest);

        String errorMessage = String.format("sid: %s. appname: %s. exception: %s. url: %s. body: %s",
                getInternalSessionId(),
                applicationName,
                ex.getMessage(),
                requestURL,
                requestBody);
        log.error(errorMessage);

        return new ResponseEntity<>(ErrorResponse.builder()
                .code(String.valueOf(httpStatus.value()))
                .session(getInternalSessionId())
                .error(ex.staticMessage())
                .build(), httpStatus);
    }
}
