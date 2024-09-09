package com.app.domain.cart.entities;


import com.app.domain.member.entities.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;

    @OneToMany(mappedBy = "cart",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    @NotNull
    @PositiveOrZero
    @Column(name = "cart_total_cost")
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "cart_active")
    private boolean active = true;

    public Cart() {
    }

    public Cart(Member owner) {
        this.owner = owner;
    }

    public void addItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
    }

    public void updateTotalCost() {
        totalCost = BigDecimal.ZERO;
        cartItems.forEach(i -> totalCost = totalCost.add(i.getTotalPrice()));
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Member getOwner() {
        return owner;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
