package com.gateway.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SEALED CLASS - Permitted subclass of GatewayEvent.
 * Represents a PII detection event in the redaction pipeline.
 */
public final class PiiEvent implements GatewayEvent {

    /**
     * RECORD - Nested record representing a single PII entity detected.
     * Demonstrates: Records can be nested inside classes.
     */
    public record PiiEntity(String type, String maskedValue, int startIndex, int endIndex) {
        @Override
        public String toString() {
            return String.format("%s at [%d-%d] -> %s", type, startIndex, endIndex, maskedValue);
        }
    }

    private final String eventId;
    private final LocalDateTime occurredAt;
    private final List<PiiEntity> entitiesDetected;
    private final String originalText;
    private final String redactedText;

    public PiiEvent(String eventId, List<PiiEntity> entitiesDetected,
                    String originalText, String redactedText) {
        this.eventId = eventId;
        this.occurredAt = LocalDateTime.now();
        this.entitiesDetected = List.copyOf(entitiesDetected); // Immutable copy
        this.originalText = originalText;
        this.redactedText = redactedText;
    }

    @Override
    public String eventId() { return eventId; }

    @Override
    public LocalDateTime occurredAt() { return occurredAt; }

    @Override
    public String description() {
        return String.format("PII detected: %d entities [%s] -> redacted",
                entitiesDetected.size(),
                String.join(", ", entitiesDetected.stream()
                        .map(PiiEntity::type)
                        .distinct()
                        .toList()));
    }

    public List<PiiEntity> entitiesDetected() { return entitiesDetected; }
    public String originalText() { return originalText; }
    public String redactedText() { return redactedText; }
}
