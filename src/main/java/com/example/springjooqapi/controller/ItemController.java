package com.example.springjooqapi.controller;

import com.example.springjooqapi.dto.ItemRequest;
import com.example.springjooqapi.dto.ItemResponse;
import com.example.springjooqapi.dto.MessageResponse;
import com.example.springjooqapi.model.UserPrincipal;
import com.example.springjooqapi.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<?> createItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ItemRequest request) {
        try {
            ItemResponse item = itemService.createItem(userPrincipal.getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error creating item: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ItemResponse> items = itemService.getAllItemsForUser(userPrincipal.getId());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        return itemService.getItemById(id, userPrincipal.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request) {
        return itemService.updateItem(id, userPrincipal.getId(), request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        boolean deleted = itemService.deleteItem(id, userPrincipal.getId());
        if (deleted) {
            return ResponseEntity.ok(new MessageResponse("Item deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}
