package com.app.domain.cart.services;

import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.entities.Cart;
import com.app.domain.cart.exceptions.CartNotFoundException;
import com.app.domain.cart.mappers.CartMapper;
import com.app.domain.cart.repositories.CartRepository;
import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.global.utils.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final MemberService memberService;

    public CartService(CartRepository cartRepository, MemberService memberService) {
        this.cartRepository = cartRepository;
        this.memberService = memberService;
    }

    public CartDTO getActiveCartDto() {
        return CartMapper.toCartDto(getActiveCart());
    }

    @Transactional
    public Cart getActiveCart() {
        Member owner = AuthUtils.getAuthenticated();
        Optional<Cart> currentCart = findActiveByOwner(owner);
        return currentCart
                .orElseGet(this::create);
    }

    private Optional<Cart> findActiveByOwner(Member owner) {
        return cartRepository.findActiveByOwner(owner);
    }

    protected Cart create() {
        Member owner = AuthUtils.getAuthenticated();
        Cart cart = new Cart(owner);
        save(cart);
        return cart;
    }

    public CartDTO findDtoById(UUID id) {
        return CartMapper.toCartDto(findById(id));
    }

    public Cart findById(UUID id) {
        return cartRepository.findById(id)
                .orElseThrow(CartNotFoundException::new);
    }

    @Transactional
    public Cart save(Cart cart) {
        cart.updateTotalCost();
        return cartRepository.save(cart);
    }


//    public Cart setInactive(Cart cart) {
//        cart.setActive(false);
//        cartRepository.save(cart);
//        return cart;
//    }

    @Transactional
    public void deleteById(UUID id) {
        Cart cart = findById(id);
        cartRepository.delete(cart);
    }

    @Transactional
    public void deleteByOwner(Long id) {
        Member member = memberService.findById(id);
        cartRepository.deleteAllByOwner(member);
    }
}