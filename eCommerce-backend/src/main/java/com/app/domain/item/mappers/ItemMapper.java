package com.app.domain.item.mappers;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemMediaDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.entities.Item;
import com.app.domain.member.entities.Member;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.app.domain.member.mappers.MemberMapper.toMemberSummaryDTO;

public class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDetailedDTO toItemDetailedDTO(Item item) {
        List<ItemMediaDTO> mediaDTOList = item.getMediaList()
                .stream()
                .map(ItemMediaMapper::toItemMediaDTO)
                .toList();
        String categoryTitle = item.getCategory() == null ? null : item.getCategory().getTitle();
        return new ItemDetailedDTO(
                item.getId(),
                item.getTitle(),
                item.getPrice(),
                item.getDescription(),
                item.getQuantity(),
                item.isActive(),
                toMemberSummaryDTO(item.getSeller()), mediaDTOList,
                categoryTitle,
                item.getCreatedDate().truncatedTo(ChronoUnit.SECONDS),
                item.getLastModifiedDate().truncatedTo(ChronoUnit.SECONDS)
        );
    }

    public static ItemSummaryDTO toItemSummaryDTO(Item item) {
        List<ItemMediaDTO> mediaDTOList = item.getMediaList()
                .stream()
                .map(ItemMediaMapper::toItemMediaDTO)
                .toList();
        return new ItemSummaryDTO(
                item.getId(),
                item.getTitle(),
                item.getPrice(),
                item.isActive(),
                mediaDTOList);
    }

    public static Item toItem(NewItemRequest request, Category category, Member seller) {
        return new Item(
                request.title(),
                request.price(),
                request.description(),
                seller,
                category);
    }
}
