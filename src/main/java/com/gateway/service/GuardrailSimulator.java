package com.gateway.service;

import com.gateway.model.GuardrailEvent;
import com.gateway.model.GuardrailEvent.GuardrailLayer;

import java.util.*;
import java.util.concurrent.*;

/**
 * GuardrailSimulator - Simulates parallel guardrail evaluation.
 *
 * Demonstrates:
 *   - CONCURRENCY: ExecutorService to manage thread pool
 *   - CALLABLE: Tasks that return results (vs Runnable which returns void)
 *   - FUTURE: Getting results from async tasks
 *   - Parallel execution of 3 guardrail layers (NeMo + LlamaGuard + Presidio)
 */
public class GuardrailSimulator {

    private final ExecutorService executorService;

    public GuardrailSimulator() {
        // Fixed thread pool with 3 threads (one per guardrail layer)
        this.executorService = Executors.newFixedThreadPool(3);
    }

    /**
     * Demonstrates: ExecutorService.invokeAll() with List<Callable>.
     *
     * Simulates parallel evaluation of a prompt through all 3 guardrail layers.
     * This mirrors SAG's real architecture where NeMo, LlamaGuard, and Presidio
     * run in parallel via Reactor Mono.zip().
     *
     * @param prompt the user prompt to evaluate
     * @return list of GuardrailEvent results from all 3 layers
     */
    public List<GuardrailEvent> evaluateAllLayers(String prompt) {
        // Create Callable tasks for each guardrail layer
        List<Callable<GuardrailEvent>> tasks = List.of(
                () -> evaluateNemo(prompt),
                () -> evaluateLlamaGuard(prompt),
                () -> evaluatePresidio(prompt)
        );

        try {
            // invokeAll() runs all Callables in parallel and blocks until all complete
            List<Future<GuardrailEvent>> futures = executorService.invokeAll(tasks);

            // Collect results from all futures
            List<GuardrailEvent> results = new ArrayList<>();
            for (Future<GuardrailEvent> future : futures) {
                results.add(future.get()); // .get() blocks until result is available
            }
            return results;

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Guardrail evaluation interrupted", e);
        }
    }

    /**
     * Demonstrates: Callable<GuardrailEvent> - individual guardrail task.
     * Simulates NVIDIA NeMo Guardrails evaluation.
     */
    private GuardrailEvent evaluateNemo(String prompt) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        System.out.printf("    [%s] NeMo Guardrails evaluating...%n", threadName);

        // Simulate processing delay (30-80ms like real NeMo)
        Thread.sleep(ThreadLocalRandom.current().nextInt(30, 80));

        boolean blocked = prompt.toLowerCase().contains("ignore instructions")
                || prompt.toLowerCase().contains("jailbreak");
        String category = blocked ? "Jailbreak attempt" : "Safe content";
        double confidence = blocked ? 0.95 : 0.05;

        System.out.printf("    [%s] NeMo Guardrails -> %s%n", threadName,
                blocked ? "BLOCKED" : "PASSED");

        return new GuardrailEvent("GR-NEMO-" + UUID.randomUUID().toString().substring(0, 8),
                GuardrailLayer.NEMO, blocked, category, confidence,
                ThreadLocalRandom.current().nextInt(30, 80));
    }

    /**
     * Simulates Meta LlamaGuard 3 evaluation.
     */
    private GuardrailEvent evaluateLlamaGuard(String prompt) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        System.out.printf("    [%s] LlamaGuard evaluating...%n", threadName);

        Thread.sleep(ThreadLocalRandom.current().nextInt(40, 90));

        boolean blocked = prompt.toLowerCase().contains("how to hack")
                || prompt.toLowerCase().contains("exploit");
        String category = blocked ? "S2: Violence/Hacking" : "Safe";
        double confidence = blocked ? 0.92 : 0.03;

        System.out.printf("    [%s] LlamaGuard -> %s%n", threadName,
                blocked ? "BLOCKED" : "PASSED");

        return new GuardrailEvent("GR-LLAMA-" + UUID.randomUUID().toString().substring(0, 8),
                GuardrailLayer.LLAMAGUARD, blocked, category, confidence,
                ThreadLocalRandom.current().nextInt(40, 90));
    }

    /**
     * Simulates Microsoft Presidio PII detection.
     */
    private GuardrailEvent evaluatePresidio(String prompt) throws InterruptedException {
        String threadName = Thread.currentThread().getName();
        System.out.printf("    [%s] Presidio PII scanning...%n", threadName);

        Thread.sleep(ThreadLocalRandom.current().nextInt(20, 60));

        boolean piiFound = prompt.matches(".*\\b\\d{3}-\\d{2}-\\d{4}\\b.*")        // SSN
                || prompt.matches(".*\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b.*") // Credit card
                || prompt.matches(".*\\b[\\w.]+@[\\w.]+\\.[a-z]{2,}\\b.*");              // Email

        String category = piiFound ? "PII detected" : "No PII";
        double confidence = piiFound ? 0.98 : 0.01;

        System.out.printf("    [%s] Presidio -> %s%n", threadName,
                piiFound ? "PII DETECTED" : "CLEAN");

        return new GuardrailEvent("GR-PRES-" + UUID.randomUUID().toString().substring(0, 8),
                GuardrailLayer.PRESIDIO, piiFound, category, confidence,
                ThreadLocalRandom.current().nextInt(20, 60));
    }

    /**
     * Demonstrates: ExecutorService.submit() with a single Callable.
     * Evaluates a single layer asynchronously.
     */
    public Future<GuardrailEvent> evaluateLayerAsync(GuardrailLayer layer, String prompt) {
        return executorService.submit(() -> switch (layer) {
            case NEMO -> evaluateNemo(prompt);
            case LLAMAGUARD -> evaluateLlamaGuard(prompt);
            case PRESIDIO -> evaluatePresidio(prompt);
        });
    }

    /**
     * Demonstrates: Proper ExecutorService shutdown.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
