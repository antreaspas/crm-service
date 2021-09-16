package com.example.crmservice.model.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import javax.validation.constraints.Size;

@Value
@Builder
public class UserUpdateRequest {
    @Size(min = 4, max = 20)
    String username;

    @Size(min = 4, max = 20)
    String password;

    @Getter(AccessLevel.NONE)
    Boolean admin;

    public Boolean isAdmin() {
        return admin;
    }
}
