package com.app.domain.item.services;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.entities.Category;
import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.mappers.ItemMapper;
import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.utils.domain.item.RandomCategoryBuilder;
import com.app.utils.domain.item.RandomItemBuilder;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@Tag("Integration test")
public class ItemServiceTest {
    private static final int PAGE_SIZE = 5;

    @Autowired
    private ItemService itemService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CategoryService categoryService;

    private final Pageable pageable0 = PageRequest.of(0, PAGE_SIZE);

    @Test
    void save_ok() {
        Item item = new RandomItemBuilder().create();
        memberService.save(item.getSeller());

        ItemSummaryDTO returnedItem = itemService.save(item);

        assertNotNull(returnedItem.id());
    }


    @Test
    void findById_ok() {
        Item item = new RandomItemBuilder().create();
        memberService.save(item.getSeller());
        itemService.save(item);

        Item returnedItem = itemService.findById(item.getId());

        assertEquals(item, returnedItem);
    }

    @Test
    void findById_throwItemWasNotFound() {
        UUID id = UUID.randomUUID();

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(id));
    }

    @Test
    void findDetailedById_ok() {
        Item item = new RandomItemBuilder().create();
        memberService.save(item.getSeller());
        itemService.save(item);
        ItemDetailedDTO itemDetailedDto = ItemMapper.toItemDetailedDTO(item);

        ItemDetailedDTO returnedDetailedItemDto = itemService.findDetailedById(item.getId());

        assertEquals(itemDetailedDto, returnedDetailedItemDto);
    }

    @Test
    void findDetailedById_throwItemWasNotFound() {
        UUID id = UUID.randomUUID();

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(id));
    }

    @Test
    void deleteById_ok() {
        Item item = new RandomItemBuilder().create();
        memberService.save(item.getSeller());
        itemService.save(item);
        assertDoesNotThrow(() -> itemService.findById(item.getId()));

        itemService.deleteById(item.getId());

        assertThrows(ItemNotFoundException.class, () -> itemService.findById(item.getId()));
    }

    @Test
    void findByCategoryId_empty() {
        Category category = new RandomCategoryBuilder().create();
        categoryService.save(category);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findByCategoryId(category.getId(), pageable0);

        assertTrue(returnedItemPage.isEmpty());
    }


    @Test
    void findByCategoryId_multiple_ofParent() {
        Category category = new RandomCategoryBuilder().create();
        Member seller = new RandomMemberBuilder().create();
        categoryService.save(category);
        memberService.save(seller);
        List<Item> itemList = new RandomItemBuilder(seller)
                .withCategory(category)
                .create(PAGE_SIZE);
        itemList.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findByCategoryId(category.getId(), pageable0);

        assertEquals(PAGE_SIZE, returnedItemPage.getNumberOfElements());
    }

    @Test
    void findByCategoryId_multiple_ofChildren() {
        Category parent = new RandomCategoryBuilder()
                .withChildren(3)
                .create();
        Category category = parent.getChildren().getFirst();
        Member seller = new RandomMemberBuilder().create();
        categoryService.save(category);
        memberService.save(seller);
        List<Item> itemList = new RandomItemBuilder(seller).withCategory(category).create(PAGE_SIZE);
        itemList.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findByCategoryId(parent.getId(), pageable0);

        assertEquals(PAGE_SIZE, returnedItemPage.getNumberOfElements());
    }


    @Test
    void findBySellerId_empty() {
        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findBySellerId(seller.getId(), pageable0);

        assertTrue(returnedItemPage.isEmpty());
    }

    @Test
    void findBySellerId_multiple() {
        Member seller = new RandomMemberBuilder().create();
        List<Item> items = new RandomItemBuilder(seller).create(PAGE_SIZE);
        memberService.save(seller);
        items.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findBySellerId(seller.getId(), pageable0);

        assertEquals(PAGE_SIZE, returnedItemPage.getNumberOfElements());
    }

    @Test
    void findByTitle_empty() {
        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findByTitle(RandomItemBuilder.getTitle(), pageable0);

        assertTrue(returnedItemPage.isEmpty());
    }

    @Test
    void findByTitle_multiple() {
        final int itemCount = 2;
        Member seller = new RandomMemberBuilder().create();
        List<Item> items = new RandomItemBuilder(seller).create(itemCount);
        String similarTitle = items.getFirst().getTitle() + StringUtils.getText(1);
        items.getLast().setTitle(similarTitle);
        memberService.save(seller);
        items.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findByTitle(items.getFirst().getTitle(), pageable0);

        assertEquals(itemCount, returnedItemPage.getNumberOfElements());
    }

    @Test
    void findAll_empty() {
        Page<ItemSummaryDTO> returnedItemPage = itemService.findAll(pageable0);

        assertTrue(returnedItemPage.isEmpty());
    }

    @Test
    void findAll_multiple() {
        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);
        List<Item> itemList = new RandomItemBuilder(seller)
                .create(PAGE_SIZE * 2);
        itemList.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedItemPage1 = itemService.findAll(pageable0);
        Page<ItemSummaryDTO> returnedItemPage2 = itemService.findAll(PageRequest.of(1, PAGE_SIZE));

        assertEquals(PAGE_SIZE, returnedItemPage1.getNumberOfElements());
        assertEquals(PAGE_SIZE, returnedItemPage2.getNumberOfElements());
    }
}