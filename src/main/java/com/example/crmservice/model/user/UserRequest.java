package com.example.crmservice.model.user;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
public class UserRequest {
    @NotBlank
    @Size(min = 4, max = 20)
    String username;

    @NotBlank
    @Size(min = 4, max = 20)
    String password;

    boolean admin;

}
