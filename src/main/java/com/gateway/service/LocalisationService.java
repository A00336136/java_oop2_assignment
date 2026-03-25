package com.gateway.service;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * LocalisationService - Internationalisation (i18n) and Localisation (l10n).
 *
 * Demonstrates:
 *   - LOCALISATION: ResourceBundle for loading locale-specific messages
 *   - Locale: Creating and switching between locales (EN, GA/Irish, FR)
 *   - MessageFormat: Parameterised messages with {0}, {1} placeholders
 *   - NumberFormat: Locale-aware number formatting
 *   - DateTimeFormatter: Locale-aware date formatting
 */
public class LocalisationService {

    private ResourceBundle bundle;
    private Locale currentLocale;

    // Supported locales
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale IRISH = Locale.of("ga", "IE");   // Irish (Gaeilge)
    public static final Locale FRENCH = Locale.FRENCH;

    public LocalisationService() {
        this(ENGLISH); // Default to English
    }

    public LocalisationService(Locale locale) {
        switchLocale(locale);
    }

    /**
     * Demonstrates: ResourceBundle.getBundle() - loads locale-specific properties.
     * Switches the active locale and reloads the resource bundle.
     */
    public void switchLocale(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("messages", locale);
        System.out.printf("  Locale switched to: %s (%s)%n",
                locale.getDisplayLanguage(Locale.ENGLISH),
                locale.toLanguageTag());
    }

    /**
     * Demonstrates: ResourceBundle.getString() - fetches a localised message.
     */
    public String getMessage(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "???" + key + "???";
        }
    }

    /**
     * Demonstrates: MessageFormat.format() - parameterised localised messages.
     * Formats a message with dynamic values like username, counts, etc.
     */
    public String getFormattedMessage(String key, Object... args) {
        String pattern = getMessage(key);
        return MessageFormat.format(pattern, args);
    }

    /**
     * Demonstrates: NumberFormat with Locale - locale-aware number formatting.
     * Formats a number according to the current locale's conventions.
     */
    public String formatNumber(long number) {
        NumberFormat nf = NumberFormat.getNumberInstance(currentLocale);
        return nf.format(number);
    }

    /**
     * Demonstrates: NumberFormat.getPercentInstance() - locale-aware percentages.
     */
    public String formatPercentage(double value) {
        NumberFormat pf = NumberFormat.getPercentInstance(currentLocale);
        pf.setMaximumFractionDigits(1);
        return pf.format(value);
    }

    /**
     * Demonstrates: DateTimeFormatter.ofLocalizedDateTime() with Locale.
     * Formats a date-time according to locale conventions.
     */
    public String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(currentLocale);
        return dateTime.format(formatter);
    }

    /**
     * Demonstrates: Full localised report generation combining all features.
     */
    public String generateLocalisedReport(long totalRequests, long blockedRequests,
                                          long piiDetections, long avgResponseMs) {
        StringBuilder report = new StringBuilder();
        report.append("\n  ").append(getMessage("report.title")).append("\n");
        report.append("  ").append("=".repeat(50)).append("\n");
        report.append("  ").append(getFormattedMessage("report.generated",
                formatDateTime(LocalDateTime.now()))).append("\n");
        report.append("  ").append(getFormattedMessage("report.total.requests",
                formatNumber(totalRequests))).append("\n");
        report.append("  ").append(getFormattedMessage("report.blocked.requests",
                formatNumber(blockedRequests))).append("\n");
        report.append("  ").append(getFormattedMessage("report.pii.detections",
                formatNumber(piiDetections))).append("\n");
        report.append("  ").append(getFormattedMessage("report.avg.response",
                formatNumber(avgResponseMs))).append("\n");

        double blockRate = totalRequests > 0 ? (double) blockedRequests / totalRequests : 0;
        report.append("  Block Rate: ").append(formatPercentage(blockRate)).append("\n");

        return report.toString();
    }

    public Locale getCurrentLocale() { return currentLocale; }
}
