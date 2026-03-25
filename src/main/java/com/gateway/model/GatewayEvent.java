package com.gateway.model;

import java.time.LocalDateTime;

/**
 * SEALED INTERFACE - Restricts which classes can implement this interface.
 * Demonstrates: Sealed classes and interfaces (Java 17+).
 *
 * Only AuthEvent, GuardrailEvent, and PiiEvent are permitted implementations.
 * This enables exhaustive pattern matching in switch expressions.
 */
public sealed interface GatewayEvent
        permits AuthEvent, GuardrailEvent, PiiEvent {

    String eventId();
    LocalDateTime occurredAt();
    String description();

    /**
     * Default method on sealed interface.
     * @return formatted log line for this event
     */
    default String toLogLine() {
        return String.format("[%s] %s: %s", occurredAt(), eventId(), description());
    }
}
