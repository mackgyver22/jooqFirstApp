package com.example.springjooqapi.service;

import com.example.springjooqapi.dto.ItemResponse;
import com.example.springjooqapi.dto.UserProfileRequest;
import com.example.springjooqapi.dto.UserProfileResponse;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import static com.example.springjooqapi.jooq.Tables.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {
    private final DSLContext dsl;

    public UserProfileService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public UserProfileResponse createOrUpdateProfile(Long userId, UserProfileRequest request) {
        // Check if profile already exists
        var existingProfile = dsl.selectFrom(USER_PROFILE)
            .where(USER_PROFILE.USER_ID.eq(userId))
            .fetchOne();

        if (existingProfile != null) {
            // Update existing profile
            dsl.update(USER_PROFILE)
                .set(USER_PROFILE.BIO, request.getBio())
                .set(USER_PROFILE.AVATAR_URL, request.getAvatarUrl())
                .set(USER_PROFILE.PHONE, request.getPhone())
                .set(USER_PROFILE.DATE_OF_BIRTH, request.getDateOfBirth())
                .set(USER_PROFILE.COUNTRY, request.getCountry())
                .set(USER_PROFILE.CITY, request.getCity())
                .set(USER_PROFILE.UPDATED_AT, LocalDateTime.now())
                .where(USER_PROFILE.USER_ID.eq(userId))
                .execute();

            return getProfileByUserId(userId).orElse(null);
        } else {
            // Create new profile
            return dsl.insertInto(USER_PROFILE)
                .set(USER_PROFILE.USER_ID, userId)
                .set(USER_PROFILE.BIO, request.getBio())
                .set(USER_PROFILE.AVATAR_URL, request.getAvatarUrl())
                .set(USER_PROFILE.PHONE, request.getPhone())
                .set(USER_PROFILE.DATE_OF_BIRTH, request.getDateOfBirth())
                .set(USER_PROFILE.COUNTRY, request.getCountry())
                .set(USER_PROFILE.CITY, request.getCity())
                .returning()
                .fetchOne()
                .into(UserProfileResponse.class);
        }
    }

    public Optional<UserProfileResponse> getProfileByUserId(Long userId) {
        // Single query with LEFT JOIN to fetch profile, user, and all items
        // This is similar to SQLAlchemy's selectinload/joinedload
        var records = dsl.select(
                        USER_PROFILE.asterisk(),
                        USERS.asterisk(),
                        ITEMS.asterisk()
                )
                .from(USER_PROFILE)
                .join(USERS).on(USER_PROFILE.USER_ID.eq(USERS.ID))
                .leftJoin(ITEMS).on(USERS.ID.eq(ITEMS.USER_ID))
                .where(USER_PROFILE.USER_ID.eq(userId))
                .fetch();

        if (records.isEmpty()) {
            return Optional.empty();
        }

        // Map the first record to profile and user info
        var firstRecord = records.get(0);
        
        UserProfileResponse response = new UserProfileResponse();
        response.setId(firstRecord.get(USER_PROFILE.ID));
        response.setUserId(firstRecord.get(USER_PROFILE.USER_ID));
        response.setBio(firstRecord.get(USER_PROFILE.BIO));
        response.setAvatarUrl(firstRecord.get(USER_PROFILE.AVATAR_URL));
        response.setPhone(firstRecord.get(USER_PROFILE.PHONE));
        response.setDateOfBirth(firstRecord.get(USER_PROFILE.DATE_OF_BIRTH));
        response.setCountry(firstRecord.get(USER_PROFILE.COUNTRY));
        response.setCity(firstRecord.get(USER_PROFILE.CITY));
        response.setCreatedAt(firstRecord.get(USER_PROFILE.CREATED_AT));
        response.setUpdatedAt(firstRecord.get(USER_PROFILE.UPDATED_AT));

        // Set nested user info
        UserProfileResponse.UserInfo userInfo = new UserProfileResponse.UserInfo(
                firstRecord.get(USERS.ID),
                firstRecord.get(USERS.USERNAME),
                firstRecord.get(USERS.EMAIL),
                firstRecord.get(USERS.FIRST_NAME),
                firstRecord.get(USERS.LAST_NAME),
                firstRecord.get(USERS.ENABLED),
                firstRecord.get(USERS.CREATED_AT),
                firstRecord.get(USERS.UPDATED_AT)
        );
        response.setUser(userInfo);

        // Collect all items from the joined records (handling the one-to-many)
        List<ItemResponse> items = records.stream()
                .filter(record -> record.get(ITEMS.ID) != null) // Filter out null items (in case user has no items)
                .map(record -> new ItemResponse(
                        record.get(ITEMS.ID),
                        record.get(ITEMS.NAME),
                        record.get(ITEMS.DESCRIPTION),
                        record.get(ITEMS.USER_ID),
                        record.get(ITEMS.CREATED_AT),
                        record.get(ITEMS.UPDATED_AT)
                ))
                .distinct() // Remove duplicates if any
                .toList();
        
        response.setItems(items);

        return Optional.of(response);
    }

    public void deleteProfile(Long userId) {
        dsl.deleteFrom(USER_PROFILE)
            .where(USER_PROFILE.USER_ID.eq(userId))
            .execute();
    }
}
