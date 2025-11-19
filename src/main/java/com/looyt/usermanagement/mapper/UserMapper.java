package com.looyt.usermanagement.mapper;

import com.looyt.usermanagement.dto.UserDTO;
import com.looyt.usermanagement.model.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    /**
     * Convert CreateUserRequest DTO to User entity
     * Sets default values for fields not in the request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDTO.CreateUserRequest request);

    /**
     * Convert User entity to UserResponse DTO
     */
    UserDTO.UserResponse toResponse(User user);

    /**
     * Convert list of User entities to list of UserResponse DTOs
     */
    List<UserDTO.UserResponse> toResponseList(List<User> users);

    /**
     * Update existing User entity with UpdateUserRequest
     * Only updates non-null fields from the request
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UserDTO.UpdateUserRequest request, @MappingTarget User user);

    /**
     * Convert Page<User> to UserListResponse with pagination metadata
     */
    @Named("pageToListResponse")
    default UserDTO.UserListResponse pageToListResponse(Page<User> page) {
        return new UserDTO.UserListResponse(
                toResponseList(page.getContent()),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}