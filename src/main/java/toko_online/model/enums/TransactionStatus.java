package toko_online.model.enums;

public enum TransactionStatus {
    PENDING,
    PAID,
    CANCELLED,
    FAILED;

    public static TransactionStatus fromBoolean(boolean isPaid) {
        return isPaid ? PAID : PENDING;
    }

    public static TransactionStatus fromString(String value) {
        if (value == null)
            return PENDING;
        try {
            return TransactionStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PENDING;
        }
    }
}
