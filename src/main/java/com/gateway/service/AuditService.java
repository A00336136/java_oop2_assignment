package com.gateway.service;

import com.gateway.model.AuditLog;
import com.gateway.model.ThreatLevel;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AuditService - Core analytics service for gateway audit logs.
 *
 * Demonstrates:
 *   - SORTING with Comparator.comparing(), thenComparing(), reversed()
 *   - LAMBDAS: Consumer, Predicate, Supplier, Function
 *   - STREAMS: Terminal operations (min, max, count, findAny, findFirst,
 *              allMatch, anyMatch, noneMatch, forEach)
 *   - STREAMS: Intermediate operations (filter, distinct, limit, map, sorted)
 *   - COLLECTORS: toMap(), groupingBy(), partitioningBy()
 */
public class AuditService {

    private final List<AuditLog> logs;

    public AuditService(List<AuditLog> logs) {
        this.logs = new ArrayList<>(logs);
    }

    // =========================================================================
    // SORTING - Comparator.comparing()
    // =========================================================================

    /**
     * Demonstrates: Comparator.comparing() with method reference.
     * Sorts audit logs by timestamp (oldest first).
     */
    public List<AuditLog> sortByTimestamp() {
        return logs.stream()
                .sorted(Comparator.comparing(AuditLog::timestamp))
                .toList();
    }

    /**
     * Demonstrates: Comparator.comparing().reversed() for descending order.
     * Sorts by response time, slowest first.
     */
    public List<AuditLog> sortByResponseTimeDescending() {
        return logs.stream()
                .sorted(Comparator.comparing(AuditLog::responseTime).reversed())
                .toList();
    }

    /**
     * Demonstrates: Chained Comparator with thenComparing().
     * Sorts by threat level (highest first), then by timestamp (newest first).
     */
    public List<AuditLog> sortByThreatThenTimestamp() {
        return logs.stream()
                .sorted(Comparator.comparing((AuditLog a) -> a.threatLevel().severity())
                        .reversed()
                        .thenComparing(AuditLog::timestamp, Comparator.reverseOrder()))
                .toList();
    }

    // =========================================================================
    // LAMBDAS - Consumer, Predicate, Supplier, Function
    // =========================================================================

    /**
     * Demonstrates: Predicate<AuditLog> - filters logs matching a condition.
     */
    public List<AuditLog> filterBy(java.util.function.Predicate<AuditLog> predicate) {
        return logs.stream()
                .filter(predicate)
                .toList();
    }

    /**
     * Demonstrates: Consumer<AuditLog> - performs an action on each log.
     */
    public void forEachLog(java.util.function.Consumer<AuditLog> action) {
        logs.forEach(action);
    }

    /**
     * Demonstrates: Function<AuditLog, R> - transforms each log into another type.
     */
    public <R> List<R> transformLogs(java.util.function.Function<AuditLog, R> mapper) {
        return logs.stream()
                .map(mapper)
                .toList();
    }

    /**
     * Demonstrates: Supplier<AuditLog> - lazy creation of a default audit log.
     */
    public AuditLog getFirstOrDefault(java.util.function.Supplier<AuditLog> defaultSupplier) {
        return logs.stream()
                .findFirst()
                .orElseGet(defaultSupplier);
    }

    // =========================================================================
    // TERMINAL OPERATIONS - min, max, count, findAny, findFirst,
    //                       allMatch, anyMatch, noneMatch, forEach
    // =========================================================================

    /**
     * Demonstrates: Stream.min() - finds log with shortest response time.
     */
    public Optional<AuditLog> fastestResponse() {
        return logs.stream()
                .min(Comparator.comparing(AuditLog::responseTime));
    }

    /**
     * Demonstrates: Stream.max() - finds log with longest response time.
     */
    public Optional<AuditLog> slowestResponse() {
        return logs.stream()
                .max(Comparator.comparing(AuditLog::responseTime));
    }

    /**
     * Demonstrates: Stream.count() - counts how many logs were blocked.
     */
    public long countBlockedRequests() {
        return logs.stream()
                .filter(AuditLog::guardrailBlocked)
                .count();
    }

    /**
     * Demonstrates: Stream.findFirst() - finds the first PII-flagged log.
     */
    public Optional<AuditLog> findFirstPiiDetection() {
        return logs.stream()
                .filter(AuditLog::piiDetected)
                .findFirst();
    }

    /**
     * Demonstrates: Stream.findAny() - finds any high-threat log (useful in parallel streams).
     */
    public Optional<AuditLog> findAnyHighThreat() {
        return logs.parallelStream()
                .filter(log -> log.threatLevel().severity() >= 3)
                .findAny();
    }

    /**
     * Demonstrates: Stream.allMatch() - checks if ALL requests were clean.
     */
    public boolean allRequestsClean() {
        return logs.stream()
                .allMatch(log -> !log.isFlagged());
    }

    /**
     * Demonstrates: Stream.anyMatch() - checks if ANY request had PII.
     */
    public boolean anyPiiDetected() {
        return logs.stream()
                .anyMatch(AuditLog::piiDetected);
    }

    /**
     * Demonstrates: Stream.noneMatch() - checks that NO critical threats exist.
     */
    public boolean noCriticalThreats() {
        return logs.stream()
                .noneMatch(log -> log.threatLevel() == ThreatLevel.CRITICAL);
    }

    // =========================================================================
    // INTERMEDIATE OPERATIONS - filter, distinct, limit, map, sorted
    // =========================================================================

    /**
     * Demonstrates: filter() + map() + distinct() - unique IPs of flagged requests.
     */
    public List<String> uniqueFlaggedIps() {
        return logs.stream()
                .filter(AuditLog::isFlagged)              // intermediate: filter
                .map(AuditLog::sourceIp)                    // intermediate: map
                .distinct()                                  // intermediate: distinct
                .sorted()                                    // intermediate: sorted
                .toList();
    }

    /**
     * Demonstrates: filter() + sorted() + limit() - top N slowest requests.
     */
    public List<AuditLog> topSlowestRequests(int n) {
        return logs.stream()
                .sorted(Comparator.comparing(AuditLog::responseTime).reversed())
                .limit(n)                                    // intermediate: limit
                .toList();
    }

    /**
     * Demonstrates: Complex pipeline combining multiple intermediate operations.
     */
    public List<String> recentBlockedUserSummaries(int limit) {
        return logs.stream()
                .filter(AuditLog::guardrailBlocked)
                .sorted(Comparator.comparing(AuditLog::timestamp).reversed())
                .limit(limit)
                .map(log -> String.format("[%s] %s from %s - Threat: %s",
                        log.timestamp().toLocalDate(),
                        log.username(),
                        log.sourceIp(),
                        log.threatLevel()))
                .toList();
    }

    // =========================================================================
    // COLLECTORS - toMap(), groupingBy(), partitioningBy()
    // =========================================================================

    /**
     * Demonstrates: Collectors.toMap() - maps user to their total request count.
     */
    public Map<String, Long> requestCountPerUser() {
        return logs.stream()
                .collect(Collectors.toMap(
                        AuditLog::username,
                        log -> 1L,
                        Long::sum     // merge function for duplicate keys
                ));
    }

    /**
     * Demonstrates: Collectors.groupingBy() - groups logs by threat level.
     */
    public Map<ThreatLevel, List<AuditLog>> groupByThreatLevel() {
        return logs.stream()
                .collect(Collectors.groupingBy(AuditLog::threatLevel));
    }

    /**
     * Demonstrates: Collectors.groupingBy() with downstream collector.
     * Groups by date and counts requests per day.
     */
    public Map<LocalDate, Long> requestsPerDay() {
        return logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.timestamp().toLocalDate(),
                        Collectors.counting()
                ));
    }

    /**
     * Demonstrates: Collectors.partitioningBy() - splits into flagged vs clean.
     */
    public Map<Boolean, List<AuditLog>> partitionByFlagged() {
        return logs.stream()
                .collect(Collectors.partitioningBy(AuditLog::isFlagged));
    }

    /**
     * Demonstrates: Collectors.groupingBy() with Collectors.averagingLong().
     * Average response time per threat level.
     */
    public Map<ThreatLevel, Double> avgResponseTimeByThreat() {
        return logs.stream()
                .collect(Collectors.groupingBy(
                        AuditLog::threatLevel,
                        Collectors.averagingLong(log -> log.responseTime().toMillis())
                ));
    }

    public List<AuditLog> getLogs() {
        return Collections.unmodifiableList(logs);
    }
}
