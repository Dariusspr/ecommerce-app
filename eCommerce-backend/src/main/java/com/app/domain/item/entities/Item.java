package com.app.domain.item.entities;

import com.app.domain.member.entities.Member;
import com.app.global.entities.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

import static com.app.global.constants.UserInputConstants.PRICE_PRECISION;
import static com.app.global.constants.UserInputConstants.PRICE_SCALE;

@Entity
@Table(name = "item")
public class Item extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_title", nullable = false)
    private String title;

    @Column(name = "item_price", nullable = false, precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal price;

    @Lob
    @Column(name = "item_description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member seller;

    protected Item() {}

    public Item(String title, BigDecimal price, Member seller) {
        this.title = title;
        this.price = price;
        this.seller = seller;
    }

    public Item(String title, BigDecimal price, String description, Member seller) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.seller = seller;
    }

    // AUTO GENERATED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Member getSeller() {
        return seller;
    }

    public void setSeller(Member seller) {
        this.seller = seller;
    }
}
