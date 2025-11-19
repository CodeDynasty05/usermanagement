package com.looyt.usermanagement.service;

import com.looyt.usermanagement.dto.UserDTO;
import com.looyt.usermanagement.exception.DuplicateResourceException;
import com.looyt.usermanagement.exception.ResourceNotFoundException;
import com.looyt.usermanagement.mapper.UserMapper;
import com.looyt.usermanagement.model.User;
import com.looyt.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO.CreateUserRequest createRequest;
    private UserDTO.UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhone("+1234567890");
        testUser.setRole(User.UserRole.USER);
        testUser.setActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        createRequest = new UserDTO.CreateUserRequest();
        createRequest.setName("John Doe");
        createRequest.setEmail("john.doe@example.com");
        createRequest.setPhone("+1234567890");
        createRequest.setRole(User.UserRole.USER);

        userResponse = new UserDTO.UserResponse();
        userResponse.setId(1L);
        userResponse.setName("John Doe");
        userResponse.setEmail("john.doe@example.com");
        userResponse.setPhone("+1234567890");
        userResponse.setRole(User.UserRole.USER);
        userResponse.setActive(true);
        userResponse.setCreatedAt(testUser.getCreatedAt());
        userResponse.setUpdatedAt(testUser.getUpdatedAt());
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserDTO.CreateUserRequest.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserDTO.UserResponse response = userService.createUser(createRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john.doe@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toEntity(any(UserDTO.CreateUserRequest.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(createRequest);
        });

        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toEntity(any(UserDTO.CreateUserRequest.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserDTO.UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    void updateUser_Success() {
        UserDTO.UpdateUserRequest updateRequest = new UserDTO.UpdateUserRequest();
        updateRequest.setName("Jane Doe");
        updateRequest.setPhone("+9876543210");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        doNothing().when(userMapper).updateEntityFromRequest(any(), any());

        UserDTO.UserResponse response = userService.updateUser(1L, updateRequest);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).updateEntityFromRequest(any(), any());
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });

        verify(userRepository, never()).deleteById(anyLong());
    }
}