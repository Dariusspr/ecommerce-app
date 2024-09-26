package com.app.domain.cart.services;

import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.dtos.requests.CartItemRequest;
import com.app.domain.cart.entities.Cart;
import com.app.domain.cart.entities.CartItem;
import com.app.domain.cart.exceptions.CartItemNotFoundException;
import com.app.domain.cart.exceptions.InsufficientStockException;
import com.app.domain.cart.mappers.CartMapper;
import com.app.domain.cart.repositories.CartItemRepository;
import com.app.domain.item.entities.Item;
import com.app.domain.item.services.ItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ItemService itemService;

    public CartItemService(CartItemRepository cartItemRepository, CartService cartService, ItemService itemService) {
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.itemService = itemService;
    }

    @Transactional
    public CartDTO addItemToCart(CartItemRequest request) {
        Cart currentCart = cartService.getCart();
        Item item = itemService.findByIdWithLock(request.itemId());
        Optional<CartItem> existingCartItem = findCartItem(currentCart, item.getId());

        int totalQuantity = getTotalQuantity(existingCartItem, request.quantity());
        if (item.getQuantity() < totalQuantity) {
            throw new InsufficientStockException();
        }

        addOrUpdateCartItem(item, existingCartItem, totalQuantity, currentCart);
        cartService.save(currentCart);
        return CartMapper.toCartDto(currentCart);
    }

    private void addOrUpdateCartItem(Item item, Optional<CartItem> existingCartItem, int totalQuantity, Cart currentCart) {
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(totalQuantity);
        } else {
            CartItem cartItem = new CartItem(item, totalQuantity);
            currentCart.addItem(cartItem);
        }
    }

    private int getTotalQuantity(Optional<CartItem> existingItem, int quantity) {
        return existingItem.map(ei -> ei.getQuantity() + quantity)
                .orElse(quantity);
    }

    @Transactional
    public CartDTO modifyCartItem(Long id, int quantity) {
        CartItem cartItem = findById(id);
        Item item = itemService.findByIdWithLock(cartItem.getItem().getId());

        if (item.getQuantity() < quantity) {
            throw new InsufficientStockException();
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return CartMapper.toCartDto(cartService.getCart());
    }


    @Transactional
    public CartDTO removeItemFromCart(Long id) {
        Cart currentCart = cartService.getCart();

        CartItem cartItem = findById(id);
        currentCart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        return CartMapper.toCartDto(currentCart);
    }

    @Transactional
    public CartDTO clear() {
        Cart currentCart = cartService.getCart();

        cartItemRepository.deleteAllByCart(currentCart);
        currentCart.setCartItems(new ArrayList<>());
        cartService.save(currentCart);

        return CartMapper.toCartDto(currentCart);
    }


    public CartItem findById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(CartItemNotFoundException::new);
    }

    private Optional<CartItem> findCartItem(Cart cart, UUID itemId) {
        List<CartItem> items = cart.getCartItems();
        return items.stream()
                .filter(i -> i
                        .getItem()
                        .getId()
                        .equals(itemId))
                .findFirst();
    }
}