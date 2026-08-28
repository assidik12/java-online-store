package toko_online.model.enums;

public enum Role {
    USER,
    ADMIN;

    public static Role fromString(String value) {
        if (value == null)
            return USER;
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
