package com.gateway.service;

import com.gateway.model.*;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * EventAnalyzer - Analyzes gateway events using pattern matching.
 *
 * Demonstrates:
 *   - SWITCH EXPRESSIONS (not statements) - returns a value
 *   - PATTERN MATCHING with sealed interface subtypes
 *   - Guarded patterns (when clause)
 *   - DATE/TIME API: LocalDateTime, Duration, DateTimeFormatter,
 *                    ZonedDateTime, Period, Instant
 */
public class EventAnalyzer {

    // =========================================================================
    // SWITCH EXPRESSIONS + PATTERN MATCHING
    // =========================================================================

    /**
     * Demonstrates: Switch expression with pattern matching on sealed interface.
     * Because GatewayEvent is sealed, the compiler knows all possible subtypes,
     * enabling EXHAUSTIVE pattern matching (no default needed).
     */
    public String analyzeEvent(GatewayEvent event) {
        return switch (event) {
            case AuthEvent auth when auth.isAuthenticated() ->
                    "AUTH_SUCCESS: User '%s' logged in with JWT %s".formatted(
                            auth.username(), auth.jwtTokenId());

            case AuthEvent auth ->
                    "AUTH_FAILURE: User '%s' rejected - %s".formatted(
                            auth.username(), auth.failureReason());

            case GuardrailEvent guard when guard.isBlocked() ->
                    "GUARDRAIL_BLOCK: %s flagged '%s' (confidence: %.0f%%)".formatted(
                            guard.layer().displayName(), guard.category(),
                            guard.confidence() * 100);

            case GuardrailEvent guard ->
                    "GUARDRAIL_PASS: %s cleared in %dms".formatted(
                            guard.layer().displayName(), guard.latencyMs());

            case PiiEvent pii when pii.entitiesDetected().size() > 3 ->
                    "PII_CRITICAL: %d entities found - IMMEDIATE REDACTION".formatted(
                            pii.entitiesDetected().size());

            case PiiEvent pii ->
                    "PII_DETECTED: %d entities [%s]".formatted(
                            pii.entitiesDetected().size(),
                            String.join(", ", pii.entitiesDetected().stream()
                                    .map(PiiEvent.PiiEntity::type)
                                    .distinct()
                                    .toList()));
        };  // No default needed - sealed interface is exhaustive!
    }

    /**
     * Demonstrates: Switch expression with pattern matching on record components.
     * Classifies threat severity of an audit log.
     */
    public String classifyThreat(AuditLog log) {
        return switch (log.threatLevel()) {
            case CRITICAL -> "CRITICAL: Immediate incident response required!";
            case HIGH     -> "HIGH: Security team notification triggered.";
            case MEDIUM   -> "MEDIUM: Added to investigation queue.";
            case LOW      -> "LOW: Logged for trend analysis.";
            case NONE     -> "CLEAN: No threats detected.";
        };
    }

    /**
     * Demonstrates: Switch expression with guards (when clause) on primitives.
     * Returns a human-readable response time assessment.
     */
    public String assessResponseTime(Duration responseTime) {
        long ms = responseTime.toMillis();
        return switch ((int) ms) {
            case int t when t < 50  -> "Excellent (<%dms)".formatted(t);
            case int t when t < 100 -> "Good (%dms)".formatted(t);
            case int t when t < 200 -> "Acceptable (%dms)".formatted(t);
            case int t when t < 500 -> "Slow (%dms) - investigate".formatted(t);
            case int t              -> "Critical (%dms) - SLA breach!".formatted(t);
        };
    }

    // =========================================================================
    // DATE/TIME API
    // =========================================================================

    /**
     * Demonstrates: LocalDateTime, DateTimeFormatter, Duration.between().
     * Generates a time-based analytics summary.
     */
    public String generateTimeSummary(LocalDateTime from, LocalDateTime to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
        Duration duration = Duration.between(from, to);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("""
                ===== Time Range Analysis =====
                From : %s
                To   : %s
                Span : %dh %dm %ds
                ===============================""",
                from.format(formatter),
                to.format(formatter),
                hours, minutes, seconds);
    }

    /**
     * Demonstrates: Duration arithmetic and comparison.
     * Calculates average response time from a list of durations.
     */
    public Duration averageResponseTime(java.util.List<Duration> durations) {
        if (durations.isEmpty()) return Duration.ZERO;

        long totalMillis = durations.stream()
                .mapToLong(Duration::toMillis)
                .sum();

        return Duration.ofMillis(totalMillis / durations.size());
    }

    /**
     * Demonstrates: LocalDateTime.now() with custom formatting for audit stamps.
     */
    public String auditTimestamp() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }
}
