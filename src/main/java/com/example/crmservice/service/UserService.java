package com.example.crmservice.service;

import com.example.crmservice.exception.UserNotFoundException;
import com.example.crmservice.exception.UsernameAlreadyExistsException;
import com.example.crmservice.model.security.UserPrincipal;
import com.example.crmservice.model.user.User;
import com.example.crmservice.model.user.UserRequest;
import com.example.crmservice.model.user.UserUpdateRequest;
import com.example.crmservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public User createUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new UsernameAlreadyExistsException();
        User user = User.builder()
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .admin(userRequest.isAdmin())
                .build();
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) userRepository.deleteById(id);
        else throw new UserNotFoundException();
    }

    public User patchUser(Long id, @Valid UserUpdateRequest updates) {
        User user = retrieveUserById(id);
        if (updates.getUsername() != null) user.setUsername(updates.getUsername());
        if (updates.getPassword() != null) user.setPassword(passwordEncoder.encode(updates.getPassword()));
        if (updates.isAdmin() != null) user.setAdmin(updates.isAdmin());
        return userRepository.save(user);
    }

    public User retrieveUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public List<User> retrieveAllUsers() {
        return userRepository.findAll();
    }

    public long countAdminUsers() {
        return userRepository.countByAdminIsTrue();
    }

}
