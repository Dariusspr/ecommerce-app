package com.app.domain.item.services;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.dtos.requests.ModifiedItemRequest;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.mappers.ItemMapper;
import com.app.domain.item.repositories.ItemRepository;
import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.utils.AuthUtil;
import com.app.global.vos.Media;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final MemberService memberService;

    public ItemService(ItemRepository itemRepository, CategoryService categoryService, MemberService memberService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.memberService = memberService;
    }

    @Transactional
    public ItemSummaryDTO create(NewItemRequest request) {
        Member seller = AuthUtil.getAuthenticated();
        List<Media> mediaList = Collections.emptyList();
//        List<Media> mediaList = AwsS3Service.upload(request.media()); // TODO
        Category category = request.categoryId() != null ? categoryService.findById(request.categoryId()) : null;
        Item item = ItemMapper.toItem(request, category, seller, mediaList);
        return save(item);
    }

    @Transactional
    public ItemSummaryDTO modify(UUID itemId, ModifiedItemRequest request) {
        Member modifier = AuthUtil.getAuthenticated();
        Item item = findById(itemId);
        Member originalSeller = item.getSeller();
        if (!modifier.equals(originalSeller)) {
            throw new ForbiddenException();
        }

        updateItemValues(item, request);

        return save(item);
    }

    private void updateItemValues(Item item, ModifiedItemRequest request) {
        if (request.title() != null) {
            item.setTitle(request.title());
        }
        if (request.price() != null) {
            item.setPrice(request.price());
        }
        if (request.description() != null) {
            item.setDescription(request.description());
        }
        if (request.media() != null) {
            item.setMediaList(Collections.emptyList()); // TODO
        }
        if (request.categoryId() != null) {
            Category category = categoryService.findById(request.categoryId());
            item.setCategory(category);
        }
    }

    @Transactional
    public ItemSummaryDTO save(Item item) {
        return ItemMapper.toItemSummaryDTO(itemRepository.save(item));
    }

    @Transactional
    public void deleteById(UUID id) {
        Member member = AuthUtil.getAuthenticated();
        Item item = findById(id);
        Member originalSeller = item.getSeller();
        if (!member.equals(originalSeller)) {
            throw new ForbiddenException();
        }
        itemRepository.delete(item);
    }

    public Item findById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
    }

    public ItemDetailedDTO findDetailedById(UUID id) {
        Item item = findById(id);
        return ItemMapper.toItemDetailedDTO(item);
    }

    public Page<ItemSummaryDTO> findByCategoryId(Long categoryId, Pageable pageable) {
        Set<Category> categories = categoryService.findFrom(categoryId);
        Page<Item> itemPage = itemRepository.findByCategories(categories, pageable);
        return itemPage.map(ItemMapper::toItemSummaryDTO);
    }

    public Page<ItemSummaryDTO> findBySellerId(Long sellerId, Pageable pageable) {
        Member seller = memberService.findById(sellerId);
        Page<Item> itemPage = itemRepository.findBySeller(seller, pageable);
        return itemPage.map(ItemMapper::toItemSummaryDTO);
    }

    public Page<ItemSummaryDTO> findByTitle(String title, Pageable pageable) {
        Page<Item> itemPage = itemRepository.findByTitle(title, pageable);
        return itemPage.map(ItemMapper::toItemSummaryDTO);
    }

    public Page<ItemSummaryDTO> findAll(Pageable pageable) {
        Page<Item> itemPage = itemRepository.findAll(pageable);
        return itemPage.map(ItemMapper::toItemSummaryDTO);
    }
}
