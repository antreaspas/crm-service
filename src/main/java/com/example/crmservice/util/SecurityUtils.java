package com.example.crmservice.util;

import com.example.crmservice.model.security.UserPrincipal;
import com.example.crmservice.model.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                ? ((UserPrincipal) authentication.getPrincipal()).getUser()
                : null;
    }

    public static String getCurrentUsername() {
        User currentUser = getCurrentUser();
        return currentUser != null
                ? currentUser.getUsername()
                : null;
    }
}
