package com.app.domain.item.services;

import com.app.domain.item.entities.ItemMedia;
import com.app.domain.item.repositories.ItemMediaRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemMediaService {

    private final ItemMediaRepository itemMediaRepository;

    public ItemMediaService(ItemMediaRepository itemMediaRepository) {
        this.itemMediaRepository = itemMediaRepository;
    }

    public ItemMedia save(ItemMedia itemMedia) {
        return itemMediaRepository.save(itemMedia);
    }

    public void delete(ItemMedia itemMedia) {
        itemMediaRepository.delete(itemMedia);
    }
}
