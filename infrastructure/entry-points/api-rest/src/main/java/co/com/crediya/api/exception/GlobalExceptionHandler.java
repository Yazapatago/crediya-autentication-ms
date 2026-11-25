package co.com.crediya.api.exception;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Convierte cualquier excepción en una respuesta JSON controlada.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String CODE_PREFIX = "AUTH-";
    private static final String MSG_ERROR_INESPERADO = "Ocurrió un error inesperado, intente más tarde";
    private static final String MSG_CUERPO_INVALIDO =
            "El cuerpo de la petición no es válido. Verifique el formato de los campos "
                    + "(salarioBase numérico, fechaNacimiento yyyy-MM-dd)";

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = resolveStatus(ex.getErrorCode());
        log.warn("Error de negocio [{}]: {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), ex.getErrorCode().getCode(), ex.getMessage(),
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado procesando {}", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), CODE_PREFIX + status.value(), MSG_ERROR_INESPERADO,
                        request.getRequestURI()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        log.warn("Cuerpo de la petición inválido: {}", ex.getMostSpecificCause().getMessage());
        return buildResponse(status, MSG_CUERPO_INVALIDO, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        log.warn("Error en la petición ({}): {}", statusCode.value(), ex.getMessage());
        return buildResponse(statusCode, messageFor(statusCode), request);
    }

    private static ResponseEntity<Object> buildResponse(HttpStatusCode status, String message, WebRequest request) {
        return ResponseEntity.status(status)
                .body(ErrorResponse.of(status.value(), CODE_PREFIX + status.value(), message, pathOf(request)));
    }

    private static HttpStatus resolveStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case EMAIL_ALREADY_REGISTERED -> HttpStatus.CONFLICT;
            case REQUIRED_FIELD, INVALID_FORMAT, SALARY_OUT_OF_RANGE -> HttpStatus.BAD_REQUEST;
        };
    }

    private static String messageFor(HttpStatusCode statusCode) {
        return switch (statusCode.value()) {
            case 404 -> "El recurso solicitado no existe";
            case 405 -> "Método HTTP no permitido para este recurso";
            case 415 -> "Tipo de contenido no soportado, use application/json";
            default -> statusCode.is5xxServerError() ? MSG_ERROR_INESPERADO : "La petición no es válida";
        };
    }

    private static String pathOf(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }
}
