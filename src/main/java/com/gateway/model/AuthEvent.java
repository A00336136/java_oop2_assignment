package com.gateway.model;

import java.time.LocalDateTime;

/**
 * SEALED CLASS - Permitted subclass of GatewayEvent.
 * Demonstrates: JEP 513 - Flexible Constructor Bodies (Java 25 preview).
 *
 * Flexible constructor bodies allow statements BEFORE the super()/this() call,
 * enabling validation of arguments before passing them to the parent constructor.
 */
public final class AuthEvent implements GatewayEvent {

    private final String eventId;
    private final LocalDateTime occurredAt;
    private final String username;
    private final boolean authenticated;
    private final String jwtTokenId;
    private final String failureReason;

    /**
     * JEP 513 - Flexible Constructor Bodies.
     * We validate parameters BEFORE they are used, which was not possible
     * in earlier Java versions without factory methods.
     */
    public AuthEvent(String eventId, String username, boolean authenticated,
                     String jwtTokenId, String failureReason) {
        // --- JEP 513: Statements BEFORE field assignments ---
        // Validate early - this is the flexible constructor body feature
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("Event ID cannot be blank");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        // --- End of pre-validation ---

        this.eventId = eventId;
        this.occurredAt = LocalDateTime.now();
        this.username = username;
        this.authenticated = authenticated;
        this.jwtTokenId = jwtTokenId != null ? jwtTokenId : "N/A";
        this.failureReason = failureReason;
    }

    @Override
    public String eventId() { return eventId; }

    @Override
    public LocalDateTime occurredAt() { return occurredAt; }

    @Override
    public String description() {
        if (authenticated) {
            return String.format("User '%s' authenticated successfully (JWT: %s)",
                    username, jwtTokenId);
        } else {
            return String.format("Authentication FAILED for user '%s': %s",
                    username, failureReason);
        }
    }

    public String username() { return username; }
    public boolean isAuthenticated() { return authenticated; }
    public String jwtTokenId() { return jwtTokenId; }
    public String failureReason() { return failureReason; }
}
