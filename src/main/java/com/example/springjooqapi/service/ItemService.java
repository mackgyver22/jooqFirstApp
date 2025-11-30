package com.example.springjooqapi.service;

import com.example.springjooqapi.dto.ItemRequest;
import com.example.springjooqapi.dto.ItemResponse;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.springjooqapi.jooq.Tables.ITEMS;

@Service
public class ItemService {

    @Autowired
    private DSLContext dsl;

    public ItemResponse createItem(Long userId, ItemRequest request) {
        return dsl.insertInto(ITEMS)
                .set(ITEMS.NAME, request.getName())
                .set(ITEMS.DESCRIPTION, request.getDescription())
                .set(ITEMS.USER_ID, userId)
                .returning()
                .fetchOne()
                .into(ItemResponse.class);
    }

    public List<ItemResponse> getAllItemsForUser(Long userId) {
        return dsl.selectFrom(ITEMS)
                .where(ITEMS.USER_ID.eq(userId))
                .fetchInto(ItemResponse.class);
    }

    public Optional<ItemResponse> getItemById(Long itemId, Long userId) {
        return Optional.ofNullable(
                dsl.selectFrom(ITEMS)
                        .where(ITEMS.ID.eq(itemId).and(ITEMS.USER_ID.eq(userId)))
                        .fetchOne()
        ).map(record -> record.into(ItemResponse.class));
    }

    public Optional<ItemResponse> updateItem(Long itemId, Long userId, ItemRequest request) {
        int updated = dsl.update(ITEMS)
                .set(ITEMS.NAME, request.getName())
                .set(ITEMS.DESCRIPTION, request.getDescription())
                .where(ITEMS.ID.eq(itemId).and(ITEMS.USER_ID.eq(userId)))
                .execute();

        if (updated == 0) {
            return Optional.empty();
        }

        return getItemById(itemId, userId);
    }

    public boolean deleteItem(Long itemId, Long userId) {
        int deleted = dsl.deleteFrom(ITEMS)
                .where(ITEMS.ID.eq(itemId).and(ITEMS.USER_ID.eq(userId)))
                .execute();

        return deleted > 0;
    }
}
