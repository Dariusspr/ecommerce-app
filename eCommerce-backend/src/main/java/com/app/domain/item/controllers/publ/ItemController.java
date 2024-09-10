package com.app.domain.item.controllers.publ;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.services.ItemService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

@RestController("publItemController")
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
    public ResponseEntity<ItemDetailedDTO> getById(
            @PathVariable("id")
            @NotNull
            UUID id) {
        ItemDetailedDTO itemDetailedDTO = itemService.findDetailedById(id);
        return ResponseEntity.ok(itemDetailedDTO);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Page<ItemSummaryDTO>> getByTitle(
            @PathVariable("title")
            @NotBlank
            @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
            String title, Pageable pageable) {
        Page<ItemSummaryDTO> itemSummaryDto = itemService.findByTitle(title, pageable);
        return ResponseEntity.ok(itemSummaryDto);
    }

    @GetMapping("/seller/{seller}")
    public ResponseEntity<Page<ItemSummaryDTO>> getBySeller(
            @PathVariable("seller")
            @NotNull
            @PositiveOrZero
            Long id, Pageable pageable) {
        Page<ItemSummaryDTO> itemSummaryDto = itemService.findBySellerId(id, pageable);
        return ResponseEntity.ok(itemSummaryDto);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ItemSummaryDTO>> getByCategory(
            @PathVariable("category")
            @NotNull
            @PositiveOrZero
            Long categoryId, Pageable pageable) {
        Page<ItemSummaryDTO> itemSummaryDto = itemService.findByCategoryId(categoryId, pageable);
        return ResponseEntity.ok(itemSummaryDto);
    }

    @GetMapping("/active")
    public ResponseEntity<Page<ItemSummaryDTO>> getByActive(
            @RequestParam(name = "active",
                    required = false,
                    defaultValue = "true")
            boolean active,
            Pageable pageable) {
        return ResponseEntity.ok(itemService.findAllByActive(active, pageable));
    }
}
