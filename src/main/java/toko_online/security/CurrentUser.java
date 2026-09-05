package toko_online.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import toko_online.exception.UnauthorizedException;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static AppUserPrincipal require() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new UnauthorizedException("Pengguna tidak terautentikasi.");
        }
        return principal;
    }

    public static AppUserPrincipal orNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof AppUserPrincipal principal)) {
            return null;
        }
        return principal;
    }
}
