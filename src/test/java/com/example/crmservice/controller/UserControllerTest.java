package com.example.crmservice.controller;

import com.example.crmservice.exception.UserNotFoundException;
import com.example.crmservice.model.user.User;
import com.example.crmservice.model.user.UserRequest;
import com.example.crmservice.model.user.UserUpdateRequest;
import com.example.crmservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.crmservice.utils.TestUtils.toJson;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Authentication tests - one endpoint should be enough as we apply role based security on the controller
    // and not its individual methods

    @Test
    public void testGetUsersUnauthenticated() throws Exception {
        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testGetUsersAuthenticatedButNotAnAdmin() throws Exception {
        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isForbidden());
    }

    // Normal tests

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUsersAuthenticatedAdmin() throws Exception {
        when(userService.retrieveAllUsers())
                .thenReturn(List.of(User.builder().build(), User.builder().build()));
        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(User.builder().build());
        UserRequest request = UserRequest.builder()
                .username("username")
                .password("password")
                .admin(false)
                .build();
        mockMvc.perform(post("/v1/users").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService).createUser(request);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateUserValidation() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(User.builder().build());
        // Empty request
        UserRequest request = UserRequest.builder().build();
        mockMvc.perform(post("/v1/users").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Request with short string lengths for username and password
        request = UserRequest.builder().username("s").password("a").build();
        mockMvc.perform(post("/v1/users").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        // Request with long string lengths for username and password
        request = UserRequest.builder()
                .username("ssssssssssssssssssssssssssssss")
                .password("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").build();
        mockMvc.perform(post("/v1/users").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(any());
        // Valid request - no admin field provided
        request = UserRequest.builder()
                .username("username")
                .password("password").build();
        mockMvc.perform(post("/v1/users").content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService).createUser(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRetrieveUser() throws Exception {
        when(userService.retrieveUserById(1L)).thenReturn(User.builder().build());
        mockMvc.perform(get("/v1/users/{userId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRetrieveUserDoesNotExist() throws Exception {
        when(userService.retrieveUserById(1L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/v1/users/{userId}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testPatchUser() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("username").build();
        when(userService.patchUser(1L, request)).thenReturn(User.builder().build());
        mockMvc.perform(patch("/v1/users/{userId}", 1).content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testPatchUserDoesNotExist() throws Exception {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("username").build();
        when(userService.patchUser(1L, request)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(patch("/v1/users/{userId}", 1).content(toJson(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/v1/users/{userId}", 1))
                .andExpect(status().isOk());
        verify(userService).deleteUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserDoesNotExist() throws Exception {
        doThrow(new UserNotFoundException()).when(userService).deleteUserById(eq(1L));
        mockMvc.perform(delete("/v1/users/{userId}", 1))
                .andExpect(status().isNotFound());
    }
}
