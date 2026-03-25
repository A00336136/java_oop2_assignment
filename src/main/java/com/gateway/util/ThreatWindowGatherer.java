package com.gateway.util;

import com.gateway.model.AuditLog;

import java.util.*;
import java.util.stream.Gatherer;

/**
 * ThreatWindowGatherer - Custom Stream Gatherer for sliding-window threat detection.
 *
 * Demonstrates:
 *   - JAVA 25 - STREAM GATHERERS (JEP 485): Custom intermediate stream operations.
 *     Gatherers extend the Stream API beyond the built-in operations (filter, map, etc.)
 *     allowing you to define stateful, many-to-many transformations.
 *
 * This gatherer implements a sliding window that detects bursts of suspicious
 * activity: if N or more flagged requests appear within a window of size W,
 * the window is emitted as an alert.
 */
public class ThreatWindowGatherer {

    /**
     * RECORD - Represents a detected threat window (burst of suspicious activity).
     */
    public record ThreatWindow(
            List<AuditLog> logs,
            int flaggedCount,
            String alertMessage
    ) {
        @Override
        public String toString() {
            return String.format("ALERT: %d flagged requests in window of %d | %s",
                    flaggedCount, logs.size(), alertMessage);
        }
    }

    /**
     * Demonstrates: Gatherer.ofSequential() - creates a custom Stream Gatherer.
     *
     * This gatherer maintains a sliding window of the last `windowSize` elements.
     * When the number of flagged elements in the window reaches `threshold`,
     * it emits a ThreatWindow alert downstream.
     *
     * @param windowSize the number of elements in the sliding window
     * @param threshold  minimum flagged count to trigger an alert
     * @return a Gatherer that transforms Stream<AuditLog> -> Stream<ThreatWindow>
     */
    public static Gatherer<AuditLog, ?, ThreatWindow> slidingThreatWindow(
            int windowSize, int threshold) {

        // Gatherer.ofSequential(initializer, integrator, finisher)
        return Gatherer.ofSequential(
                // INITIALIZER: Creates the mutable state (the sliding window buffer)
                () -> new ArrayDeque<AuditLog>(windowSize),

                // INTEGRATOR: Called for each element; decides what to emit downstream
                Gatherer.Integrator.ofGreedy((window, element, downstream) -> {
                    // Add new element to the window
                    window.addLast(element);

                    // Remove oldest if window exceeds size
                    if (window.size() > windowSize) {
                        window.removeFirst();
                    }

                    // Only check when window is full
                    if (window.size() == windowSize) {
                        long flaggedCount = window.stream()
                                .filter(AuditLog::isFlagged)
                                .count();

                        // If threshold met, emit a ThreatWindow alert
                        if (flaggedCount >= threshold) {
                            String users = window.stream()
                                    .filter(AuditLog::isFlagged)
                                    .map(AuditLog::username)
                                    .distinct()
                                    .reduce((a, b) -> a + ", " + b)
                                    .orElse("unknown");

                            ThreatWindow alert = new ThreatWindow(
                                    List.copyOf(window),
                                    (int) flaggedCount,
                                    "Suspicious burst from user(s): " + users
                            );
                            downstream.push(alert);
                        }
                    }
                    return true; // continue processing
                })
        );
    }

    /**
     * Demonstrates: Another Gatherer - Fixed-size batch grouping.
     * Groups stream elements into fixed-size batches.
     *
     * @param batchSize number of elements per batch
     * @return a Gatherer that transforms Stream<AuditLog> -> Stream<List<AuditLog>>
     */
    public static Gatherer<AuditLog, ?, List<AuditLog>> batchOf(int batchSize) {
        return Gatherer.<AuditLog, ArrayList<AuditLog>, List<AuditLog>>ofSequential(
                ArrayList::new,

                Gatherer.Integrator.ofGreedy((ArrayList<AuditLog> batch, AuditLog element,
                                              Gatherer.Downstream<? super List<AuditLog>> downstream) -> {
                    batch.add(element);
                    if (batch.size() >= batchSize) {
                        downstream.push(new ArrayList<>(batch));
                        batch.clear();
                    }
                    return true;
                }),

                // FINISHER: Called after all elements processed; emits remaining batch
                (ArrayList<AuditLog> batch,
                 Gatherer.Downstream<? super List<AuditLog>> downstream) -> {
                    if (!batch.isEmpty()) {
                        downstream.push(new ArrayList<>(batch));
                    }
                }
        );
    }
}
