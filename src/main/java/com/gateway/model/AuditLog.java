package com.gateway.model;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * RECORD - Immutable data carrier for audit log entries.
 * Demonstrates: Java Records (auto-generates constructor, getters, equals, hashCode, toString).
 *
 * Each audit log captures a single request through the AI Gateway pipeline.
 */
public record AuditLog(
        String id,
        String username,
        String prompt,
        String response,
        LocalDateTime timestamp,
        Duration responseTime,
        boolean piiDetected,
        boolean guardrailBlocked,
        ThreatLevel threatLevel,
        String sourceIp
) implements Comparable<AuditLog> {

    /**
     * Compact constructor with validation.
     * Demonstrates: Record compact constructor.
     */
    public AuditLog {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt must not be blank");
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (threatLevel == null) {
            threatLevel = ThreatLevel.NONE;
        }
    }

    /**
     * Custom method on a Record.
     * @return true if this request was flagged by any security layer
     */
    public boolean isFlagged() {
        return piiDetected || guardrailBlocked || threatLevel.severity() > 0;
    }

    /**
     * Demonstrates: Comparable implementation for natural ordering by timestamp.
     */
    @Override
    public int compareTo(AuditLog other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}
