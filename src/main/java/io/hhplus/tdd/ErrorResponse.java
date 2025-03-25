package io.hhplus.tdd;

import lombok.Setter;

public record ErrorResponse(
        String code,
        String message
) {
}
