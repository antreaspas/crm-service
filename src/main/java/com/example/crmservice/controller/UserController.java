package com.example.crmservice.controller;

import com.example.crmservice.model.user.UserRequest;
import com.example.crmservice.model.user.UserResponse;
import com.example.crmservice.model.user.UserUpdateRequest;
import com.example.crmservice.service.UserService;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "Get all existing users", notes = "Required roles: ADMIN")
    @GetMapping
    public List<UserResponse> retrieveAllUsers() {
        return userService.retrieveAllUsers().stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    @ApiOperation(value = "Create a new user", notes = "Required roles: ADMIN")
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
        return UserResponse.fromEntity(userService.createUser(userRequest));
    }

    @ApiOperation(value = "Get an existing user by their ID", notes = "Required roles: ADMIN")
    @GetMapping("/{userId}")
    public UserResponse retrieveUserById(@PathVariable Long userId) {
        return UserResponse.fromEntity(userService.retrieveUserById(userId));
    }

    @ApiOperation(value = "Update an existing user by their ID", notes = "Required roles: ADMIN")
    @PatchMapping("/{userId}")
    public UserResponse patchUser(@PathVariable Long userId,
                                  @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return UserResponse.fromEntity(userService.patchUser(userId, userUpdateRequest));
    }

    @ApiOperation(value = "Delete an existing user by their ID", notes = "Required roles: ADMIN")
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
    }
}
