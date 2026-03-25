package com.gateway.model;

/**
 * ENUM with fields and methods.
 * Demonstrates: Enum with constructor, fields, and behaviour.
 */
public enum ThreatLevel {
    NONE(0, "No threat detected"),
    LOW(1, "Minor policy deviation"),
    MEDIUM(2, "Potential jailbreak attempt"),
    HIGH(3, "Active attack pattern"),
    CRITICAL(4, "Data exfiltration attempt");

    private final int severity;
    private final String description;

    ThreatLevel(int severity, String description) {
        this.severity = severity;
        this.description = description;
    }

    public int severity() {
        return severity;
    }

    public String description() {
        return description;
    }

    /**
     * Demonstrates: Static factory method using streams.
     */
    public static ThreatLevel fromSeverity(int severity) {
        return java.util.Arrays.stream(values())
                .filter(t -> t.severity == severity)
                .findFirst()
                .orElse(NONE);
    }
}
