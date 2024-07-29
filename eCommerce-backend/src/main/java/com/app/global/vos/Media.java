package com.app.global.vos;

import com.app.global.enums.FileFormat;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record Media(String title, String url, @Enumerated(EnumType.STRING) FileFormat format) {
}
