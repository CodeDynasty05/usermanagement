package com.looyt.usermanagement.dto;

import com.looyt.usermanagement.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User Event DTO for Kafka messages
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    private Long userId;
    private String eventType; // CREATED, UPDATED, DELETED
    private String name;
    private String email;
    private String phone;
    private User.UserRole role;
    private Boolean active;
    private LocalDateTime timestamp;
    private String performedBy; // Who performed the action (for audit)

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }

    public static UserEvent createEvent(Long userId, String eventType, UserDTO.UserResponse user, String performedBy) {
        return new UserEvent(
                userId,
                eventType,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getActive(),
                LocalDateTime.now(),
                performedBy
        );
    }
}