package com.aops.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {
    }

    private static Authentication getAuthentication() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        return authentication;
    }

    public static Long getId() {

        return Long.valueOf(
                getAuthentication().getName()
        );
    }

    public static String getRole() {

        return getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority()
                                .replace("ROLE_", "")
                )
                .orElseThrow(() ->
                        new IllegalStateException("User has no role"));
    }

    public static boolean hasRole(String role) {

        return getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_" + role));
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isUser() {
        return hasRole("USER");
    }
}