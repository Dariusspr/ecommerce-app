package com.app.domain.item.entities;

import com.app.domain.member.entities.Member;
import com.app.global.entities.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.app.global.constants.UserInputConstants.*;

@Entity
@Table(name = "item")
public class Item extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @NotBlank
    @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
    @Column(name = "item_title", nullable = false, length = TITLE_LENGTH_MAX)
    private String title;

    @NotNull
    @Column(name = "item_price", nullable = false, precision = PRICE_PRECISION, scale = PRICE_SCALE)
    private BigDecimal price;

    @Lob
    @Column(name = "item_description")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member seller;

    @OneToMany(mappedBy = "item")
    private List<ItemMedia> mediaList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

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

    public void addMedia(ItemMedia media) {
        if (media == null) {
            throw new IllegalArgumentException("'media' is null");
        }
        media.setItem(this);
        mediaList.add(media);
    }

    public void removeMedia(ItemMedia media) {
        if (media == null) {
            throw new IllegalArgumentException("'media' is null");
        }
        media.setItem(null);
        mediaList.remove(media);
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

    public List<ItemMedia> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<ItemMedia> media) {
        this.mediaList = media;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
