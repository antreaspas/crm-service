package com.example.crmservice.service;

import com.example.crmservice.exception.LastAdminUserException;
import com.example.crmservice.exception.UserNotFoundException;
import com.example.crmservice.exception.UsernameAlreadyExistsException;
import com.example.crmservice.model.user.User;
import com.example.crmservice.model.user.UserRequest;
import com.example.crmservice.model.user.UserUpdateRequest;
import com.example.crmservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    public void testLoadUserByUsernameReturnsPrincipal() {
        when(userRepository.findByUsername(eq("user")))
                .thenReturn(Optional.of(User.builder().username("user").build()));
        assertThat(userService.loadUserByUsername("user").getUsername())
                .isEqualTo("user");
    }

    @Test
    public void testLoadUserByUsernameThrowsWhenUsernameIsNotFound() {
        when(userRepository.findByUsername(eq("user")))
                .thenReturn(Optional.empty());
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userService.loadUserByUsername("user"));
    }

    @Test
    public void testCreateUser() {
        when(userRepository.existsByUsername(eq("user"))).thenReturn(false);
        UserRequest request = UserRequest.builder()
                .username("user")
                .password("pass")
                .build();
        userService.createUser(request);
        verify(userRepository).save(argThat(user -> !user.isAdmin() && user.getUsername().equals("user")));
    }

    @Test
    public void testCreateUserThrowsWhenUsernameAlreadyExists() {
        when(userRepository.existsByUsername(eq("user"))).thenReturn(true);
        assertThatExceptionOfType(UsernameAlreadyExistsException.class)
                .isThrownBy(() -> userService.createUser(UserRequest.builder()
                        .username("user")
                        .password("pass")
                        .build()));
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testDeleteUserById() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(
                User.builder()
                        .username("username")
                        .admin(true)
                        .build()));
        when(userRepository.countByAdminIsTrue()).thenReturn(2L);
        userService.deleteUserById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    public void testDeleteUserByIdThrowsWhenIdDoesNotExist() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.deleteUserById(1L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    public void testDeleteUserByIdThrowsWhenUserIsTheLastAdminUser() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(
                User.builder()
                        .id(1L)
                        .username("username")
                        .admin(true)
                        .build()));
        when(userRepository.countByAdminIsTrue()).thenReturn(1L);
        assertThatExceptionOfType(LastAdminUserException.class).isThrownBy(() -> userService.deleteUserById(1L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    public void testPatchUser() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(
                User.builder()
                        .username("oldUsername")
                        .admin(true)
                        .build()));
        userService.patchUser(1L, UserUpdateRequest.builder()
                .username("newUsername")
                .build());
        verify(userRepository).save(argThat(user -> user.isAdmin() && user.getUsername().equals("newUsername")));
    }

    @Test
    public void testRetrieveUserById() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(
                User.builder()
                        .username("username")
                        .admin(true)
                        .build()));
        assertThat(userService.retrieveUserById(1L))
                .extracting("username", "admin")
                .containsExactly("username", true);
    }

    @Test
    public void testRetrieveUserByIdThrowsWhenIdDoesNotExist() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());
        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.retrieveUserById(1L));
    }
}
