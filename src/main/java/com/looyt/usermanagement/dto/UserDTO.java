package com.looyt.usermanagement.dto;

import com.looyt.usermanagement.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
        private String phone;

        private User.UserRole role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String name;

        @Email(message = "Email should be valid")
        private String email;

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number should be valid")
        private String phone;

        private User.UserRole role;

        private Boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private User.UserRole role;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserListResponse {
        private java.util.List<UserResponse> users;
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private int pageSize;
    }
}