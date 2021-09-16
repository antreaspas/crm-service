package com.example.crmservice.model.user;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
    Long id;

    String username;

    boolean admin;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .admin(user.isAdmin())
                .build();
    }
}
