package com.looyt.usermanagement.controller;

import com.looyt.usermanagement.dto.UserDTO;
import com.looyt.usermanagement.model.User;
import com.looyt.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users with Kafka event streaming")
public class UserController {

    private final UserService userService;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the service is running")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Management Service is running with Kafka!");
    }

    @PostMapping
    @Operation(
            summary = "Create a new user",
            description = "Creates a new user and publishes a CREATED event to Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User with email already exists")
    })
    public ResponseEntity<UserDTO.UserResponse> createUser(
            @Valid @RequestBody @Parameter(description = "User creation request") UserDTO.CreateUserRequest request
    ) {
        UserDTO.UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserDTO.UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO.UserResponse> getUserById(
            @PathVariable @Parameter(description = "User ID") Long id
    ) {
        UserDTO.UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Retrieves all users with pagination, sorting, and filtering options"
    )
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserDTO.UserListResponse.class)))
    public ResponseEntity<UserDTO.UserListResponse> getAllUsers(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-indexed)") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "id") @Parameter(description = "Sort by field") String sortBy,
            @RequestParam(defaultValue = "asc") @Parameter(description = "Sort direction (asc/desc)") String sortDir,
            @RequestParam(required = false) @Parameter(description = "Filter by role") User.UserRole role,
            @RequestParam(required = false) @Parameter(description = "Filter by active status") Boolean active,
            @RequestParam(required = false) @Parameter(description = "Filter by name (partial match)") String nameFilter
    ) {
        UserDTO.UserListResponse response = userService.getAllUsers(page, size, sortBy, sortDir, role, active, nameFilter);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update user",
            description = "Updates an existing user and publishes an UPDATED event to Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Email already in use")
    })
    public ResponseEntity<UserDTO.UserResponse> updateUser(
            @PathVariable @Parameter(description = "User ID") Long id,
            @Valid @RequestBody @Parameter(description = "User update request") UserDTO.UpdateUserRequest request
    ) {
        UserDTO.UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user",
            description = "Deletes a user and publishes a DELETED event to Kafka"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Parameter(description = "User ID") Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}