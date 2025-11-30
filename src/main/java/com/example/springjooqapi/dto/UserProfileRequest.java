package com.example.springjooqapi.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileRequest {
    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    private String bio;
    
    @Size(max = 500, message = "Avatar URL cannot exceed 500 characters")
    private String avatarUrl;
    
    @Size(max = 20, message = "Phone cannot exceed 20 characters")
    private String phone;
    
    private LocalDate dateOfBirth;
    
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;
    
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;
}
