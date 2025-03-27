package io.hhplus.tdd.point.exception;

import lombok.Setter;

public record ErrorResponse(
        String code,
        String message
) {
}
