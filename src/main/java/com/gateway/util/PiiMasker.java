package com.gateway.util;

import com.gateway.model.PiiEvent;
import com.gateway.model.PiiEvent.PiiEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PiiMasker - PII detection and redaction utility.
 *
 * Demonstrates:
 *   - LAMBDAS: Function, Predicate, Consumer, UnaryOperator, BiFunction
 *   - Functional composition with andThen() and compose()
 *   - Method references
 *   - Pattern matching with regex
 */
public class PiiMasker {

    /**
     * RECORD - Represents a PII detection rule.
     * Demonstrates: Record used as a functional data structure.
     */
    public record PiiRule(String name, Pattern pattern, UnaryOperator<String> masker) {}

    private final List<PiiRule> rules;

    public PiiMasker() {
        this.rules = new ArrayList<>();
        initDefaultRules();
    }

    private void initDefaultRules() {
        // Demonstrates: Lambda as UnaryOperator<String>
        rules.add(new PiiRule("EMAIL",
                Pattern.compile("\\b[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}\\b"),
                text -> "[EMAIL_REDACTED]"));

        rules.add(new PiiRule("SSN",
                Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b"),
                text -> "[SSN_REDACTED]"));

        rules.add(new PiiRule("CREDIT_CARD",
                Pattern.compile("\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b"),
                text -> "[CC_REDACTED]"));

        rules.add(new PiiRule("PHONE_US",
                Pattern.compile("\\b\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}\\b"),
                text -> "[PHONE_REDACTED]"));

        rules.add(new PiiRule("IP_ADDRESS",
                Pattern.compile("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b"),
                text -> "[IP_REDACTED]"));

        rules.add(new PiiRule("IBAN",
                Pattern.compile("\\b[A-Z]{2}\\d{2}[A-Z0-9]{4}\\d{7}([A-Z0-9]?){0,16}\\b"),
                text -> "[IBAN_REDACTED]"));
    }

    /**
     * Demonstrates: Function<String, String> composition.
     * Creates a single masking function by chaining all rules.
     */
    public Function<String, String> buildMaskingPipeline() {
        // Start with identity function
        Function<String, String> pipeline = Function.identity();

        // Chain each rule's masking function using andThen()
        for (PiiRule rule : rules) {
            Function<String, String> ruleFunction = input -> {
                Matcher matcher = rule.pattern().matcher(input);
                return matcher.replaceAll(match -> rule.masker().apply(match.group()));
            };
            pipeline = pipeline.andThen(ruleFunction);
        }
        return pipeline;
    }

    /**
     * Demonstrates: Predicate<String> - checks if text contains any PII.
     */
    public Predicate<String> containsPii() {
        return text -> rules.stream()
                .anyMatch(rule -> rule.pattern().matcher(text).find());
    }

    /**
     * Demonstrates: Consumer<String> - logs PII detection results.
     */
    public Consumer<String> logPiiDetection() {
        return text -> {
            rules.stream()
                    .filter(rule -> rule.pattern().matcher(text).find())
                    .forEach(rule -> System.out.printf("    PII DETECTED [%s]: pattern matched%n",
                            rule.name()));
        };
    }

    /**
     * Demonstrates: BiFunction<String, List<PiiRule>, PiiEvent> - creates event from scan.
     */
    public BiFunction<String, String, PiiEvent> createPiiEvent() {
        return (eventId, text) -> {
            List<PiiEntity> entities = new ArrayList<>();
            String redacted = text;

            for (PiiRule rule : rules) {
                Matcher matcher = rule.pattern().matcher(redacted);
                while (matcher.find()) {
                    entities.add(new PiiEntity(
                            rule.name(),
                            rule.masker().apply(matcher.group()),
                            matcher.start(),
                            matcher.end()));
                }
                redacted = matcher.replaceAll(match -> rule.masker().apply(match.group()));
            }

            return new PiiEvent(eventId, entities, text, redacted);
        };
    }

    /**
     * Demonstrates: Supplier<List<String>> - lazy retrieval of supported PII types.
     */
    public Supplier<List<String>> supportedPiiTypes() {
        return () -> rules.stream()
                .map(PiiRule::name)
                .toList();
    }

    /**
     * Convenience method: mask all PII in text.
     */
    public String mask(String text) {
        return buildMaskingPipeline().apply(text);
    }
}
