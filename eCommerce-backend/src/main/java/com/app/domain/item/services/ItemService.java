package com.app.domain.item.services;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.dtos.requests.ModifyItemRequest;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.entities.Item;
import com.app.domain.item.entities.ItemMedia;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.mappers.ItemMapper;
import com.app.domain.item.mappers.ItemMediaMapper;
import com.app.domain.item.repositories.ItemRepository;
import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.utils.AuthUtils;
import com.app.global.services.MediaService;
import com.app.global.vos.Media;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final MediaService mediaService;
    private final ItemMediaService itemMediaService;

    public ItemService(ItemRepository itemRepository, CategoryService categoryService, MemberService memberService, MediaService mediaService, ItemMediaService itemMediaService) {
        this.itemRepository = itemRepository;
        this.categoryService = categoryService;
        this.memberService = memberService;
        this.mediaService = mediaService;
        this.itemMediaService = itemMediaService;
    }

    @Transactional
    public ItemSummaryDTO create(NewItemRequest request) {
        Member seller = AuthUtils.getAuthenticated();
        Category category = request.categoryId() != null ? categoryService.findById(request.categoryId()) : null;
        Item item = ItemMapper.toItem(request, category, seller);
        List<ItemMedia> itemMediaList = uploadAndMapMedia(request.media());
        item.addAllMedia(itemMediaList);
        return save(item);
    }

    @Transactional
    public ItemSummaryDTO modify(UUID itemId, ModifyItemRequest request) {
        Item item = findByIdWithLock(itemId);
        if (isNotAllowedItemModifier(item.getSeller())) {
            throw new ForbiddenException();
        }
        updateItemValues(item, request);
        return save(item);
    }

    @Transactional
    public ItemSummaryDTO save(Item item) {
        return ItemMapper.toItemSummaryDTO(itemRepository.saveAndFlush(item));
    }

    @Transactional
    public void deleteById(UUID id) {
        Item item = findByIdWithLock(id);
        if (isNotAllowedItemModifier(item.getSeller())) {
            throw new ForbiddenException();
        }
        deleteAllCurrentMedia(item);
        itemRepository.delete(item);
    }

    public Item findById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);
    }

    public Item findByIdWithLock(UUID id) {
        return itemRepository.findByIdWithLock(id)
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

    public Item validateAndReduceQuantity(UUID itemId, int reduceQuantity) {
        Item item = findByIdWithLock(itemId);

        int currentAvailableQuantity = item.getQuantity();
        if (currentAvailableQuantity < reduceQuantity) {
            throw new IllegalArgumentException();
        }
        item.setQuantity(currentAvailableQuantity - reduceQuantity);
        save(item);

        return item;
    }

    // TODO: find methods for only active items

    // TODO: update active state

    // Helpers

    private List<ItemMedia> uploadAndMapMedia(List<MultipartFile> multipartFileList) {
        List<Media> itemMediaList = mediaService.uploadAndGet(multipartFileList);
        return itemMediaList.stream().map(ItemMediaMapper::toItemMedia).toList();
    }

    private void updateItemValues(Item item, ModifyItemRequest request) {
        System.out.println(request);
        if (!request.title().isEmpty()) {
            item.setTitle(request.title());
        }
        if (request.price() != null) {
            item.setPrice(request.price());
        }
        if (request.quantity() != null) {
            item.setQuantity(request.quantity());
        }
        if (request.active() != null) {
            item.setActive(request.active());
        }
        if (request.description() != null && !request.description().isEmpty()) {
            item.setDescription(request.description());
        }
        List<MultipartFile> multipartFileList = request.media();
        if (multipartFileList != null && !multipartFileList.isEmpty() && !multipartFileList.getFirst().isEmpty()) {
            updateMedia(item, multipartFileList);
        }
        if (request.categoryId() != null) {
            Category category = categoryService.findById(request.categoryId());
            item.setCategory(category);
        }
    }

    private void updateMedia(Item item, List<MultipartFile> media) {
        deleteAllCurrentMedia(item);
        List<ItemMedia> newItemMediaList = uploadAndMapMedia(media);
        item.addAllMedia(newItemMediaList);
    }

    private void deleteAllCurrentMedia(Item item) {
        List<ItemMedia> currentItemMediaList = item.getMediaList();
        currentItemMediaList.forEach(m -> {
            mediaService.delete(m.getMedia().key());
            itemMediaService.delete(m);
        });
        item.setMediaList(new ArrayList<>());
    }

    private boolean isNotAllowedItemModifier(Member seller) {
        Member modifier = AuthUtils.getAuthenticated();
        return !modifier.equals(seller) && !modifier.isAdmin();
    }
}
