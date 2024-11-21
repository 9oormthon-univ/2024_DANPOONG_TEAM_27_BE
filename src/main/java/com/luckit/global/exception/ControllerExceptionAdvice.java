package com.luckit.global.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.global.template.ApiResponseTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponseTemplate<String>> handleCustomException(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(ApiResponseTemplate.error(e.getErrorCode()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (existing, replacement) -> existing));

        return ResponseEntity.status(ErrorCode.VALIDATION_EXCEPTION.getHttpStatus())
                .body(ApiResponseTemplate.<String>error(ErrorCode.VALIDATION_EXCEPTION)
                        .toBuilder()
                        .data(convertMapToString(errorMap))
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseTemplate<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Throwable mostSpecificCause = e.getMostSpecificCause();

        if (mostSpecificCause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) mostSpecificCause;
            if (ife.getTargetType().isEnum()) {
                String enumValues = getEnumValues(ife.getTargetType());
                String message = String.format("허용되는 값: [%s]", enumValues);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseTemplate.<String>error(ErrorCode.INVALID_ENUM_VALUE)
                                .toBuilder()
                                .data(message)
                                .build());
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseTemplate.error(ErrorCode.JSON_SYNTAX_ERROR));
    }

    private static String getEnumValues(Class<?> enumClass) {
        if (!enumClass.isEnum()) {
            throw new CustomException(ErrorCode.INVALID_ENUM_VALUE, ErrorCode.INVALID_ENUM_VALUE.getMessage());
        }

        return Stream.of(enumClass.getEnumConstants())
                .map(e -> ((Enum<?>) e).name())
                .collect(Collectors.joining(", "));
    }

    private String convertMapToString(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
