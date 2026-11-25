package co.com.crediya.model.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    REQUIRED_FIELD("AUTH-001"),
    INVALID_FORMAT("AUTH-002"),
    SALARY_OUT_OF_RANGE("AUTH-003"),
    EMAIL_ALREADY_REGISTERED("AUTH-004");

    private final String code;
}
