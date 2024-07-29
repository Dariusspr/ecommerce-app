package com.app.domain.item.entities;

import com.app.global.vos.Media;
import jakarta.persistence.*;

@Entity
@Table(name = "item_media")
public class ItemMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_media_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "title", column = @Column(name = "media_title")),
            @AttributeOverride(name = "url", column = @Column(name = "media_url")),
            @AttributeOverride(name = "format", column = @Column(name = "media_format")),
    })
    private Media media;

    @Column(name = "media_altText")
    private String altText;

    protected ItemMedia() {}

    public ItemMedia(Item item, Media media, String altText) {
        this.item = item;
        this.media = media;
        this.altText = altText;
    }

    // AUTO GENERATED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}
