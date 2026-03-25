package com.gateway.model;

import java.time.LocalDateTime;

/**
 * SEALED CLASS - Permitted subclass of GatewayEvent.
 * Represents a guardrail evaluation event (NeMo, LlamaGuard, or Presidio).
 */
public final class GuardrailEvent implements GatewayEvent {

    /**
     * ENUM representing the three guardrail layers in SAG.
     */
    public enum GuardrailLayer {
        NEMO("NVIDIA NeMo Guardrails"),
        LLAMAGUARD("Meta LlamaGuard 3"),
        PRESIDIO("Microsoft Presidio v2.2");

        private final String displayName;

        GuardrailLayer(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() { return displayName; }
    }

    private final String eventId;
    private final LocalDateTime occurredAt;
    private final GuardrailLayer layer;
    private final boolean blocked;
    private final String category;
    private final double confidence;
    private final long latencyMs;

    public GuardrailEvent(String eventId, GuardrailLayer layer, boolean blocked,
                          String category, double confidence, long latencyMs) {
        this.eventId = eventId;
        this.occurredAt = LocalDateTime.now();
        this.layer = layer;
        this.blocked = blocked;
        this.category = category;
        this.confidence = confidence;
        this.latencyMs = latencyMs;
    }

    @Override
    public String eventId() { return eventId; }

    @Override
    public LocalDateTime occurredAt() { return occurredAt; }

    @Override
    public String description() {
        return String.format("%s [%s]: %s (confidence: %.1f%%, latency: %dms)",
                layer.displayName(), blocked ? "BLOCKED" : "PASSED",
                category, confidence * 100, latencyMs);
    }

    public GuardrailLayer layer() { return layer; }
    public boolean isBlocked() { return blocked; }
    public String category() { return category; }
    public double confidence() { return confidence; }
    public long latencyMs() { return latencyMs; }
}
