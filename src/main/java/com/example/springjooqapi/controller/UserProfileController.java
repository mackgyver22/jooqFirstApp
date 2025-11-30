package com.example.springjooqapi.controller;

import com.example.springjooqapi.dto.MessageResponse;
import com.example.springjooqapi.dto.UserProfileRequest;
import com.example.springjooqapi.dto.UserProfileResponse;
import com.example.springjooqapi.model.UserPrincipal;
import com.example.springjooqapi.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return userProfileService.getProfileByUserId(userPrincipal.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> createOrUpdateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserProfileRequest request) {
        UserProfileResponse profile = userProfileService.createOrUpdateProfile(
            userPrincipal.getId(), 
            request
        );
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        userProfileService.deleteProfile(userPrincipal.getId());
        return ResponseEntity.ok(new MessageResponse("Profile deleted successfully"));
    }
}
