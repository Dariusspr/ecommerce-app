package com.app.domain.item.controllers.publ;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.services.ItemService;
import com.app.global.constants.RestEndpoints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("publicItemController")
@RequestMapping(ItemController.BASE_URL)
public class ItemController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/items";

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<Page<ItemSummaryDTO>> getAll(Pageable pageable) {
        Page<ItemSummaryDTO> itemSummaryDto = itemService.findAll(pageable);
        return ResponseEntity.ok(itemSummaryDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailedDTO> getById(@PathVariable("id")UUID id) {
        ItemDetailedDTO itemDetailedDTO = itemService.findDetailedById(id);
        return ResponseEntity.ok(itemDetailedDTO);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Page<ItemSummaryDTO>> getByTitle(@PathVariable("title") String title, Pageable pageable) {
        Page<ItemSummaryDTO> itemSummaryDto = itemService.findByTitle(title, pageable);
        return ResponseEntity.ok(itemSummaryDto);
    }

    @GetMapping("/seller/{seller}")
    public ResponseEntity<Page<ItemSummaryDTO>> getBySeller(@PathVariable("seller") Long id, Pageable pageable) {
        Page<ItemSummaryDTO> itemSummaryDto = itemService.findBySellerId(id, pageable);
        return ResponseEntity.ok(itemSummaryDto);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ItemSummaryDTO>> getByCategory(@PathVariable("category") Long category, Pageable pageable) {
        Page<ItemSummaryDTO> itemSummaryDto = itemService.findByCategoryId(category, pageable);
        return ResponseEntity.ok(itemSummaryDto);
    }
}
