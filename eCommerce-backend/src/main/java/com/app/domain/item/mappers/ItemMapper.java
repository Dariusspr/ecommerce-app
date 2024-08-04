package com.app.domain.item.mappers;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemMediaDTO;
import com.app.domain.item.entities.Item;

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
        return new ItemDetailedDTO(
                item.getId(),
                item.getTitle(),
                item.getPrice(),
                item.getDescription(),
                toMemberSummaryDTO(item.getSeller()), mediaDTOList,
                item.getCategory().getTitle(),
                item.getCreatedDate(),
                item.getLastModifiedDate()
        );
    }
}
