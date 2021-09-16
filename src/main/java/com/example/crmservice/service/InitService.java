package com.example.crmservice.service;

import com.example.crmservice.model.user.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Perform any required initialisation tasks here
 * <p>
 * Tasks:
 * <ul>
 *     <li>Seed the database with an initial admin user if no admin users are found.
 *     The user's password is generated using a random string and is logged to the console.</li>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InitService {

    private final UserService userService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (userService.countAdminUsers() == 0) {
            String password = RandomStringUtils.randomAlphanumeric(10);
            userService.createUser(
                    UserRequest.builder()
                            .username("admin")
                            .password(password)
                            .admin(true)
                            .build()
            );
            log.info("Created initial admin user with password: {}", password);
        }
    }
}
