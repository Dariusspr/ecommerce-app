package com.app.domain.cart.entities;

import com.app.domain.item.entities.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @PositiveOrZero
    @Column(name = "cart_item_quantity")
    private int quantity;

    @NotNull
    @PositiveOrZero
    @Column(name = "cart_item_price")
    private BigDecimal pricePerUnit;

    protected CartItem() {
    }

    public CartItem(Item item, int quantity, BigDecimal pricePerUnit) {
        this.item = item;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    public BigDecimal getTotalPrice() {
        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    // AUTO GENERATED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
