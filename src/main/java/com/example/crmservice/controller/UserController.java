package com.example.crmservice.controller;

import com.example.crmservice.model.user.UserRequest;
import com.example.crmservice.model.user.UserResponse;
import com.example.crmservice.model.user.UserUpdateRequest;
import com.example.crmservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> retrieveAllUsers() {
        return userService.retrieveAllUsers().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return UserResponse.fromEntity(userService.createUser(userRequest));
    }

    @GetMapping("/{userId}")
    public UserResponse retrieveUserById(@PathVariable Long userId) {
        return UserResponse.fromEntity(userService.retrieveUserById(userId));
    }

    @PatchMapping("/{userId}")
    public UserResponse patchUser(@PathVariable Long userId,
                                  @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return UserResponse.fromEntity(userService.patchUser(userId, userUpdateRequest));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
