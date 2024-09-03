package com.app.domain.item.controllers.members;

import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.dtos.requests.ModifiedItemRequest;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.services.ItemService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController("membersItemController")
@RequestMapping(ItemController.BASE_URL)
@PreAuthorize("hasAnyRole({'MEMBER', 'ADMIN'})")
public class ItemController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/items";

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemSummaryDTO> create(@Validated NewItemRequest request) {
        return ResponseEntity.ok(itemService.create(request));
    }

    @PutMapping(value = "/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemSummaryDTO> modify(
            @PathVariable
            @NotNull
            UUID itemId,
            @Validated
            ModifiedItemRequest request) {
        return ResponseEntity.ok(itemService.modify(itemId, request));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> delete(
            @PathVariable
            @NotNull
            UUID itemId) {
        itemService.deleteById(itemId);
        return ResponseEntity.ok().build();
    }
}
