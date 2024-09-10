package com.app.utils.domain.item;

import com.app.domain.item.entities.Category;
import com.app.domain.item.entities.Item;
import com.app.domain.item.entities.ItemMedia;
import com.app.domain.member.entities.Member;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

public class RandomItemBuilder {
    private static final int DESCRIPTION_LENGTH_MAX = 200;
    private static final int MEDIA_COUNT_MAX = 10;

    private Member customSeller;
    private boolean withCategory;
    private Category customCategory;
    private boolean withMedia;
    private boolean withId;
    private boolean withAuditable;
    private boolean withActive;

    public RandomItemBuilder() {
    }

    public RandomItemBuilder(Member customSeller) {
        this.customSeller = customSeller;
    }

    public RandomItemBuilder withMedia() {
        withMedia = true;
        return this;
    }

    public RandomItemBuilder withCategory() {
        withCategory = true;
        return this;
    }

    public RandomItemBuilder withCategory(Category customCategory) {
        this.customCategory = customCategory;
        return withCategory();
    }

    public RandomItemBuilder withId() {
        this.withId = true;
        return this;
    }

    public RandomItemBuilder withActive() {
        this.withActive = true;
        return this;
    }

    public Item create() {
        Member seller = Objects.requireNonNullElseGet(
                customSeller,
                () -> new RandomMemberBuilder().create());
        Item item = createBasicItem(seller);
        if (withId)
            setId(item);
        if (withCategory)
            setCategory(item);
        if (withMedia)
            generateAndBindMedia(item);
        if (withAuditable)
            setAuditable(item);
        if (withActive)
            item.setActive(true);
        return item;
    }

    private void setId(Item item) {
        item.setId(UUID.randomUUID());
    }

    public List<Item> create(int count) {
        List<Item> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add(create());
        }
        return items;
    }

    private Item createBasicItem(Member seller) {
        String title = getTitle();
        BigDecimal price = NumberUtils.getPrice();
        String description = StringUtils.getText(DESCRIPTION_LENGTH_MAX);
        return new Item(title, price, description, seller);
    }

    private void setCategory(Item item) {
        Category category = Objects.requireNonNullElse(
                customCategory,
                new RandomCategoryBuilder().create());
        item.setCategory(category);
    }

    private void generateAndBindMedia(Item item) {
        int mediaCount = NumberUtils.getIntegerInRange(1, MEDIA_COUNT_MAX);
        List<ItemMedia> medias = ItemMediaUtils.getItemMedia(mediaCount);
        medias.forEach(item::addMedia);
    }

    public static String getTitle() {
        return RandomStringUtils.randomAlphanumeric(TITLE_LENGTH_MIN, TITLE_LENGTH_MAX);
    }

    public void setAuditable(Item item) {
        item.setCreatedDate(LocalDateTime.now());
        item.setLastModifiedDate(LocalDateTime.now());
    }

    public RandomItemBuilder withAuditable() {
        withAuditable = true;
        return this;
    }
}
