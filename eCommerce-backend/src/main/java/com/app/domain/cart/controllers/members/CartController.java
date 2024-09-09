package com.app.domain.cart.controllers.members;

import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.services.CartService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("membersCartController")
@RequestMapping(CartController.BASE_URL)
@PreAuthorize("hasRole('MEMBER')")
public class CartController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/carts";

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping()
    public ResponseEntity<CartDTO> getActiveCart() {
        return ResponseEntity.ok(cartService.getActiveCartDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCartById(
            @PathVariable("id")
            @NotNull
            UUID id) {
        return ResponseEntity.ok(cartService.findDtoById(id));
    }
}