package com.app.domain.cart.services;

import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.dtos.CartItemDTO;
import com.app.domain.cart.dtos.requests.CartItemRequest;
import com.app.domain.cart.entities.Cart;
import com.app.domain.cart.exceptions.CartItemNotFoundException;
import com.app.domain.cart.exceptions.InsufficientStockException;
import com.app.domain.cart.repositories.CartItemRepository;
import com.app.domain.cart.repositories.CartRepository;
import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.repositories.ItemRepository;
import com.app.domain.item.services.ItemService;
import com.app.domain.member.entities.Member;
import com.app.domain.member.repositories.MemberRepository;
import com.app.domain.member.services.MemberService;
import com.app.utils.domain.item.RandomItemBuilder;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.NumberUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {

    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    private Member member;
    private Cart cart;
    private Item item;

    @BeforeAll
    void setupMemberAndCart() {
        member = new RandomMemberBuilder().create();
        memberService.save(member);

        cart = new Cart(member);
        cartService.save(cart);

        item = createItem(5);
    }

    @AfterAll
    void finalClear() {
        itemRepository.deleteAll();
        cartRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @BeforeEach
    void mockAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(member);
    }

    @AfterEach
    void clear() {
        cartItemRepository.deleteAll();
    }

    @Test
    void addItemToCart_sameItem_ok() {
        CartItemRequest request = new CartItemRequest(item.getId(), 1);

        CartDTO cartDTO = cartItemService.addItemToCart(request);

        assertEquals(1, cartDTO.items().size());
        assertEquals(1, cartDTO.items().getFirst().quantity());

        cartDTO = cartItemService.addItemToCart(request);
        assertEquals(1, cartDTO.items().size());
        assertEquals(2, cartDTO.items().getFirst().quantity());
    }

    @Test
    void addItemToCart_throwInsufficientStock() {
        CartItemRequest request = new CartItemRequest(item.getId(), 6);

        assertThrows(InsufficientStockException.class,
                () -> cartItemService.addItemToCart(request));
    }

    @Test
    void addItemToCart_throwItemNotFound() {
        CartItemRequest request = new CartItemRequest(UUID.randomUUID(), 1);

        assertThrows(ItemNotFoundException.class,
                () -> cartItemService.addItemToCart(request));
    }

    @Test
    void removeItemFromCart_ok() {
        CartItemRequest request = new CartItemRequest(item.getId(), 3);
        CartDTO createdCart = cartItemService.addItemToCart(request);
        CartItemDTO cartItem = createdCart.items().getFirst();

        CartDTO updatedCart = cartItemService.removeItemFromCart(cartItem.cardId());

        assertNotEquals(createdCart.items(), updatedCart.items());
    }

    @Test
    void removeItemFromCart_throwCartItemNotFound() {
        assertThrows(CartItemNotFoundException.class,
                () -> cartItemService.removeItemFromCart(NumberUtils.getId()));
    }


    @Test
    void modifyCartItem_ok() {
        final int newQuantity = 1;
        CartItemRequest request = new CartItemRequest(item.getId(), 2);
        CartDTO createdCart = cartItemService.addItemToCart(request);
        CartItemDTO cartItem = createdCart.items().getFirst();

        CartDTO updatedCart = cartItemService.modifyCartItem(cartItem.cardId(), newQuantity);

        assertEquals(newQuantity, updatedCart.items().getFirst().quantity());
    }

    @Test
    void modifyCartItem_throwInsufficientStock() {
        CartItemRequest request = new CartItemRequest(item.getId(), 2);
        CartDTO createdCart = cartItemService.addItemToCart(request);
        CartItemDTO cartItem = createdCart.items().getFirst();

        assertThrows(InsufficientStockException.class,
                () -> cartItemService.modifyCartItem(cartItem.cardId(), 6));
    }

    @Test
    void modifyCartItem_throwCartItemNotFound() {
        assertThrows(CartItemNotFoundException.class,
                () -> cartItemService.modifyCartItem(NumberUtils.getId(), 1));
    }

    @Test
    void clearActive_ok() {
        Item item2 = createItem(1);
        Item item3 = createItem(1);
        CartItemRequest request = new CartItemRequest(item.getId(), 1);
        cartItemService.addItemToCart(request);
        request = new CartItemRequest(item2.getId(), 1);
        cartItemService.addItemToCart(request);
        request = new CartItemRequest(item3.getId(), 1);
        CartDTO cart = cartItemService.addItemToCart(request);

        assertEquals(3, cart.items().size());
        CartDTO updatedCart = cartItemService.clearActive();
        assertEquals(0, updatedCart.items().size());
    }

    private Item createItem(int quantity) {
        Item item = new RandomItemBuilder().create();
        item.setQuantity(quantity);
        memberService.save(item.getSeller());
        itemService.save(item);
        return item;
    }
}