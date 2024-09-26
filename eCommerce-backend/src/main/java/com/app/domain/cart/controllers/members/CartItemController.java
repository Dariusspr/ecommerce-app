package com.app.domain.cart.controllers.members;


import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.dtos.requests.CartItemRequest;
import com.app.domain.cart.services.CartItemService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController("membersCartItemController")
@RequestMapping(CartItemController.BASE_URL)
@PreAuthorize("hasRole('MEMBER')")
public class CartItemController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/cart-items";

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<CartDTO> addItem(
            @RequestBody
            @Validated
            CartItemRequest request) {
        return ResponseEntity.ok(cartItemService.addItemToCart(request));
    }

    @PutMapping("/{itemId}/{quantity}")
    public ResponseEntity<CartDTO> modifyCartItem(
            @PathVariable("itemId")
            @NotNull
            Long itemId,
            @PathVariable("quantity")
            @Positive
            int quantity) {
        return ResponseEntity.ok(cartItemService.modifyCartItem(itemId, quantity));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<CartDTO> deleteItemFromCart(
            @PathVariable("itemId")
            @NotNull
            Long itemId) {
        return ResponseEntity.ok(cartItemService.removeItemFromCart(itemId));
    }

    @DeleteMapping
    public ResponseEntity<CartDTO> clearActiveCart() {
        return ResponseEntity.ok((cartItemService.clear()));
    }
}
