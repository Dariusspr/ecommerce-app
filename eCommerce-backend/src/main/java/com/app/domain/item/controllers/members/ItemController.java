package com.app.domain.item.controllers.members;

import com.app.domain.item.dtos.requests.ModifiedItemRequest;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.services.ItemService;
import com.app.global.constants.RestEndpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController("adminItemController")
@RequestMapping(ItemController.BASE_URL)
@PreAuthorize("hasAnyRole({'MEMBER', 'ADMIN'})")
public class ItemController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/items";

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewItemRequest request) {
        return ResponseEntity.ok(itemService.create(request));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> modify(@PathVariable UUID itemId, @RequestBody ModifiedItemRequest request) {
        return ResponseEntity.ok(itemService.modify(itemId, request));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> delete(@PathVariable UUID itemId) {
        itemService.deleteById(itemId);
        return ResponseEntity.ok().build();
    }
}
