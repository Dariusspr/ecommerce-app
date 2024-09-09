package com.app.domain.cart.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CartItemRequest(

        @NotNull
        UUID itemId,

        @Positive
        int quantity) {
}