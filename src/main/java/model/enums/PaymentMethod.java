package model.enums;

public enum PaymentMethod {
    BCA("BCA"),
    CASH("Cash"),
    ALFAMART("Alfamart"),
    MANDIRI("Mandiri");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromString(String value) {
        if (value == null) return CASH;
        for (PaymentMethod method : values()) {
            if (method.name().equalsIgnoreCase(value.trim()) || method.displayName.equalsIgnoreCase(value.trim())) {
                return method;
            }
        }
        return CASH;
    }
}
