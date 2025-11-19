package com.looyt.usermanagement.service;

import com.looyt.usermanagement.dto.UserDTO;
import com.looyt.usermanagement.dto.UserEvent;
import com.looyt.usermanagement.exception.ResourceNotFoundException;
import com.looyt.usermanagement.exception.DuplicateResourceException;
import com.looyt.usermanagement.kafka.UserEventProducer;
import com.looyt.usermanagement.mapper.UserMapper;
import com.looyt.usermanagement.model.User;
import com.looyt.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventProducer eventProducer;

    public UserDTO.UserResponse createUser(UserDTO.CreateUserRequest request) {
        log.info("Creating new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User with email {} already exists", request.getEmail());
            throw new DuplicateResourceException("User with email " + request.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(request);

        if (user.getRole() == null) {
            user.setRole(User.UserRole.USER);
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        UserDTO.UserResponse response = userMapper.toResponse(savedUser);

        try {
            UserEvent event = UserEvent.createEvent(
                    savedUser.getId(),
                    UserEvent.EventType.CREATED.name(),
                    response,
                    "SYSTEM"
            );
            eventProducer.publishUserCreatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish user created event for user ID: {}", savedUser.getId(), e);
            // Don't fail the operation if Kafka is down
        }

        return response;
    }

    public UserDTO.UserResponse getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        return userMapper.toResponse(user);
    }

    public UserDTO.UserListResponse getAllUsers(int page, int size, String sortBy, String sortDir,
                                                User.UserRole role, Boolean active, String nameFilter) {
        log.info("Fetching users - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage;

        if (role != null) {
            userPage = userRepository.findByRole(role, pageable);
        } else if (active != null) {
            userPage = userRepository.findByActive(active, pageable);
        } else if (nameFilter != null && !nameFilter.isEmpty()) {
            userPage = userRepository.findByNameContainingIgnoreCase(nameFilter, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        // Use MapStruct to convert Page to ListResponse
        return userMapper.pageToListResponse(userPage);
    }

    public UserDTO.UserResponse updateUser(Long id, UserDTO.UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        // Check for email uniqueness if email is being changed
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                log.error("User with email {} already exists", request.getEmail());
                throw new DuplicateResourceException("User with email " + request.getEmail() + " already exists");
            }
        }

        userMapper.updateEntityFromRequest(request, user);

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());

        UserDTO.UserResponse response = userMapper.toResponse(updatedUser);

        try {
            UserEvent event = UserEvent.createEvent(
                    updatedUser.getId(),
                    UserEvent.EventType.UPDATED.name(),
                    response,
                    "SYSTEM"
            );
            eventProducer.publishUserUpdatedEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish user updated event for user ID: {}", updatedUser.getId(), e);
        }

        return response;
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        UserDTO.UserResponse response = userMapper.toResponse(user);

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);

        try {
            UserEvent event = UserEvent.createEvent(
                    id,
                    UserEvent.EventType.DELETED.name(),
                    response,
                    "SYSTEM"
            );
            eventProducer.publishUserDeletedEvent(event);
        } catch (Exception e) {
            log.error("Failed to publish user deleted event for user ID: {}", id, e);
        }
    }
}