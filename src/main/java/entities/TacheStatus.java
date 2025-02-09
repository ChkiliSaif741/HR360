package entities;

public enum TacheStatus {
    A_FAIRE("A faire"),
    EN_COURS("En cours"),
    TERMINEE("Terminée");
    private final String value;

    TacheStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TacheStatus fromValue(String value) {
        for (TacheStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
