package com.app.global.vos;

import com.app.global.enums.FileFormat;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record Media(
        @NotBlank
        String title,

        @NotBlank
        String key,

        @NotBlank
        String url,

        @NotNull
        @Enumerated(EnumType.STRING)
        FileFormat format) {
}
