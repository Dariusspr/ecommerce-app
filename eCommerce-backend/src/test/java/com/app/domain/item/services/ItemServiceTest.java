package com.app.domain.item.services;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.dtos.requests.ModifyItemRequest;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.mappers.ItemMapper;
import com.app.domain.item.repositories.CategoryRepository;
import com.app.domain.item.repositories.ItemRepository;
import com.app.domain.member.entities.Member;
import com.app.domain.member.repositories.MemberRepository;
import com.app.domain.member.services.MemberService;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.services.MediaService;
import com.app.utils.domain.item.RandomCategoryBuilder;
import com.app.utils.domain.item.RandomItemBuilder;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemServiceTest {
    private static final int PAGE_SIZE = 5;

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private MediaService mediaService;

    private final Pageable pageable_0_5 = PageRequest.of(0, PAGE_SIZE);

    private Item item;

    @BeforeEach
    void setup() {
        item = new RandomItemBuilder().create();
        memberService.save(item.getSeller());
    }

    @AfterEach
    void clear() {
        itemRepository.deleteAll();
        categoryRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void save_ok() {
        ItemSummaryDTO returnedItem = itemService.save(item);

        assertNotNull(returnedItem.id());
    }


    @Test
    void findById_ok() {
        itemService.save(item);

        Item returnedItem = itemService.findById(item.getId());

        assertEquals(item, returnedItem);
    }

    @Test
    void findById_throwItemWasNotFound() {
        UUID id = UUID.randomUUID();

        assertThrows(ItemNotFoundException.class,
                () -> itemService.findById(id));
    }

    @Test
    void findDetailedById_ok() {
        itemService.save(item);
        ItemDetailedDTO itemDetailedDto = ItemMapper.toItemDetailedDTO(item);

        ItemDetailedDTO returnedDetailedItemDto = itemService.findDetailedById(item.getId());

        assertEquals(itemDetailedDto, returnedDetailedItemDto);
    }

    @Test
    void findDetailedById_throwItemWasNotFound() {
        UUID id = UUID.randomUUID();

        assertThrows(ItemNotFoundException.class,
                () -> itemService.findById(id));
    }

    @Test
    void deleteById_ok() {
        itemService.save(item);
        mockAuthentication(item.getSeller());
        assertDoesNotThrow(() -> itemService.findById(item.getId()));

        itemService.deleteById(item.getId());

        assertThrows(ItemNotFoundException.class,
                () -> itemService.findById(item.getId()));
    }

    @Test
    void deleteById_notSameMember_throwForbiddenException() {
        itemService.save(item);
        assertDoesNotThrow(() -> itemService.findById(item.getId()));
        Member anotherMember = new RandomMemberBuilder().create();
        memberService.save(anotherMember);
        mockAuthentication(anotherMember);

        assertThrows(ForbiddenException.class,
                () -> itemService.deleteById(item.getId()));
    }

    @Test
    void findByCategoryId_empty() {
        Category category = new RandomCategoryBuilder().create();
        categoryService.save(category);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findAllByCategoryId(category.getId(), pageable_0_5);

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

        Page<ItemSummaryDTO> returnedItemPage = itemService.findAllByCategoryId(category.getId(), pageable_0_5);

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

        Page<ItemSummaryDTO> returnedItemPage = itemService.findAllByCategoryId(parent.getId(), pageable_0_5);

        assertEquals(PAGE_SIZE, returnedItemPage.getNumberOfElements());
    }


    @Test
    void findBySellerId_empty() {
        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findAllBySellerId(seller.getId(), pageable_0_5);

        assertTrue(returnedItemPage.isEmpty());
    }

    @Test
    void findBySellerId_multiple() {
        Member seller = new RandomMemberBuilder().create();
        List<Item> items = new RandomItemBuilder(seller).create(PAGE_SIZE);
        memberService.save(seller);
        items.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findAllBySellerId(seller.getId(), pageable_0_5);

        assertEquals(PAGE_SIZE, returnedItemPage.getNumberOfElements());
    }

    @Test
    void findByTitle_empty() {
        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);

        Page<ItemSummaryDTO> returnedItemPage = itemService.findAllByTitle(RandomItemBuilder.getTitle(), pageable_0_5);

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

        Page<ItemSummaryDTO> returnedItemPage = itemService.findAllByTitle(items.getFirst().getTitle(), pageable_0_5);

        assertEquals(itemCount, returnedItemPage.getNumberOfElements());
    }

    @Test
    void findAll_empty() {
        Page<ItemSummaryDTO> returnedItemPage = itemService.findAll(pageable_0_5);

        assertTrue(returnedItemPage.isEmpty());
    }

    @Test
    void findAll_multiple() {
        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);
        List<Item> itemList = new RandomItemBuilder(seller)
                .create(PAGE_SIZE * 2);
        itemList.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedItemPage1 = itemService.findAll(pageable_0_5);
        Page<ItemSummaryDTO> returnedItemPage2 = itemService.findAll(PageRequest.of(1, PAGE_SIZE));

        assertEquals(PAGE_SIZE, returnedItemPage1.getNumberOfElements());
        assertEquals(PAGE_SIZE, returnedItemPage2.getNumberOfElements());
    }

    @Test
    void findAllByActive_ok() {
        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);
        List<Item> inactiveItemList = new RandomItemBuilder(seller)
                .create(PAGE_SIZE - 1);
        List<Item> activeItemList = new RandomItemBuilder(seller)
                .withActive()
                .create(PAGE_SIZE);
        inactiveItemList.forEach(itemService::save);
        activeItemList.forEach(itemService::save);

        Page<ItemSummaryDTO> returnedActivePage = itemService.findAllByActive(true, pageable_0_5);
        Page<ItemSummaryDTO> returnedInactivePage = itemService.findAllByActive(false, pageable_0_5);

        assertEquals(PAGE_SIZE - 1, returnedInactivePage.getNumberOfElements());
        assertEquals(PAGE_SIZE, returnedActivePage.getNumberOfElements());
    }

    @Test
    void create_ok() {
        mockAuthentication(item.getSeller());
        NewItemRequest request = new NewItemRequest(item.getTitle(), item.getPrice(), item.getDescription(), Collections.emptyList(), null);

        ItemSummaryDTO itemSummaryDTO = itemService.create(request);

        assertDoesNotThrow(() -> itemService.findById(itemSummaryDTO.id()));
    }

    @Test
    void modify__newTitle_ok() {
        mockAuthentication(item.getSeller());
        NewItemRequest request = new NewItemRequest(item.getTitle(), item.getPrice(), item.getDescription(), Collections.emptyList(), null);
        ItemSummaryDTO itemSummaryDTO1 = itemService.create(request);
        assertDoesNotThrow(() -> itemService.findById(itemSummaryDTO1.id()));
        final String newTitle = "NewTitle";
        ModifyItemRequest modifyItemRequest = new ModifyItemRequest(newTitle,
                null, null, null,
                null, null, null);

        ItemSummaryDTO itemSummaryDTO2 = itemService.modify(itemSummaryDTO1.id(), modifyItemRequest);

        assertDoesNotThrow(() -> itemService.findById(itemSummaryDTO2.id()));
        assertEquals(itemSummaryDTO1.id(), itemSummaryDTO2.id());
        assertEquals(newTitle, itemSummaryDTO2.title());
    }

    @Test
    void modify_notSameMember_throwForbiddenException() {
        mockAuthentication(item.getSeller());
        NewItemRequest request = new NewItemRequest(item.getTitle(), item.getPrice(), item.getDescription(), Collections.emptyList(), null);
        ItemSummaryDTO itemSummaryDTO1 = itemService.create(request);
        assertDoesNotThrow(() -> itemService.findById(itemSummaryDTO1.id()));
        final String newTitle = "NewTitle";
        ModifyItemRequest modifyItemRequest = new ModifyItemRequest(newTitle,
                null, null, null,
                null, null, null);
        Member otherSeller = new RandomMemberBuilder().create();
        mockAuthentication(otherSeller);

        assertThrows(ForbiddenException.class, () -> itemService.modify(itemSummaryDTO1.id(), modifyItemRequest));
    }

    private void mockAuthentication(Member member) {
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(member);
    }
}