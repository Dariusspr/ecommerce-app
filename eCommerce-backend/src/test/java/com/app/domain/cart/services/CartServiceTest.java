package com.app.domain.cart.services;


import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.dtos.requests.CartItemRequest;
import com.app.domain.cart.entities.Cart;
import com.app.domain.cart.entities.CartItem;
import com.app.domain.cart.exceptions.CartNotFoundException;
import com.app.domain.cart.repositories.CartItemRepository;
import com.app.domain.cart.repositories.CartRepository;
import com.app.domain.item.entities.Item;
import com.app.domain.item.repositories.ItemRepository;
import com.app.domain.item.services.ItemService;
import com.app.domain.member.entities.Member;
import com.app.domain.member.repositories.MemberRepository;
import com.app.domain.member.services.MemberService;
import com.app.utils.domain.item.RandomItemBuilder;
import com.app.utils.domain.member.RandomMemberBuilder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    private Member member;

    @BeforeAll
    void setupMember() {
        member = new RandomMemberBuilder().create();
        memberService.save(member);
    }

    @AfterAll
    void finalClear() {
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
        itemRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    void getCart_create() {
        Cart cart = cartService.getCart();

        assertNotNull(cart.getOwner());
        assertEquals(BigDecimal.ZERO, cart.getTotalCost().setScale(0));
    }

    @Test
    @Transactional
    void getCart_returnOld() {
        Cart old = createAndSaveCartWithItem();

        Cart oldCart = cartService.getCart();

        List<CartItem> oldItems = old.getCartItems();
        List<CartItem> returnedItems = oldCart.getCartItems();
        assertNotNull(oldCart.getOwner());
        assertEquals(oldItems.size(), returnedItems.size());
    }

    @Test
    void getCartDto() {
        CartDTO cartDTO = cartService.getCartDto();

        assertNotNull(cartDTO.cartId());
        assertEquals(Collections.emptyList(), cartDTO.items());
        assertEquals(BigDecimal.ZERO, cartDTO.totalCost().setScale(0));
    }

    @Test
    void getCartDto_withItems() {
        Item item2 = createItem(1);
        Item item1 = createItem(2);

        CartItemRequest request = new CartItemRequest(item1.getId(), 2);
        cartItemService.addItemToCart(request);
        request = new CartItemRequest(item2.getId(), 1);
        cartItemService.addItemToCart(request);

        CartDTO cartDTO = cartService.getCartDto();

        assertNotNull(cartDTO.cartId());
        assertEquals(2, cartDTO.items().size());
        BigDecimal totalCost = item1.getPrice().multiply(BigDecimal.TWO).add(item2.getPrice());
        assertEquals(totalCost.setScale(0, RoundingMode.HALF_UP),
                cartDTO.totalCost().setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    void findById_ok() {
        Cart oldCart = cartService.getCart();
        Cart returnedCart = cartService.findById(oldCart.getId());

        assertNotNull(returnedCart.getOwner());
        assertEquals(BigDecimal.ZERO, returnedCart.getTotalCost().setScale(0));
    }

    @Test
    void findById_throwCartNotFound() {
        assertThrows(CartNotFoundException.class,
                () -> cartService.findById(UUID.randomUUID()));
    }

    @Test
    void findDtoById_ok() {
        Cart oldCart = cartService.getCart();
        CartDTO returnedCart = cartService.findDtoById(oldCart.getId());

        assertNotNull(returnedCart.cartId());
        assertEquals(BigDecimal.ZERO, returnedCart.totalCost().setScale(0));
    }

    @Test
    void deleteById_ok() {
        Cart cart = cartService.getCart();

        assertDoesNotThrow(() -> cartService.deleteById(cart.getId()));
        assertThrows(CartNotFoundException.class,
                () -> cartService.deleteById(cart.getId()));
    }

    @Test
    void deleteByOwner_ok() {
        Cart cart = cartService.getCart();

        assertDoesNotThrow(() -> cartService.deleteByOwner(cart.getOwner().getId()));
        assertThrows(CartNotFoundException.class,
                () -> cartService.findById(cart.getId()));
    }

    private Cart createAndSaveCartWithItem() {
        Cart cart = cartService.getCart();
        Item item = createItem(1);
        CartItem cartItem = new CartItem(item, 1);
        cart.addItem(cartItem);
        cartService.save(cart);
        return cart;
    }

    private Item createItem(int quantity) {
        Item item = new RandomItemBuilder().create();
        item.setQuantity(quantity);
        memberService.save(item.getSeller());
        itemService.save(item);
        return item;
    }
}