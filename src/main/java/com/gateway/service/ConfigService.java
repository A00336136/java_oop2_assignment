package com.gateway.service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * ConfigService - Configuration and file management using NIO2 API.
 *
 * Demonstrates:
 *   - NIO2: Path, Paths, Files
 *   - Files.readAllLines(), Files.writeString(), Files.write()
 *   - Files.walk() for directory traversal
 *   - Files.readAttributes() for file metadata
 *   - Files.exists(), Files.createDirectories()
 *   - try-with-resources with Stream<Path>
 */
public class ConfigService {

    private final Path configDir;
    private final Path logsDir;

    public ConfigService(Path baseDir) {
        this.configDir = baseDir.resolve("config");
        this.logsDir = baseDir.resolve("logs");
    }

    /**
     * Demonstrates: Files.createDirectories(), Files.writeString(), Path.resolve().
     * Initializes the gateway configuration directory structure.
     */
    public void initializeDirectories() throws IOException {
        // NIO2: Create nested directories (like mkdir -p)
        Files.createDirectories(configDir);
        Files.createDirectories(logsDir);

        // NIO2: Write default config if it doesn't exist
        Path defaultConfig = configDir.resolve("gateway.properties");
        if (!Files.exists(defaultConfig)) {
            String config = """
                    # AI Gateway Configuration
                    gateway.name=Secure AI Gateway
                    gateway.version=2.0.0
                    gateway.port=8080

                    # Guardrail settings
                    guardrails.nemo.enabled=true
                    guardrails.llamaguard.enabled=true
                    guardrails.presidio.enabled=true
                    guardrails.presidio.min-score=0.6

                    # JWT settings
                    jwt.algorithm=HMAC-SHA384
                    jwt.expiry-hours=1
                    jwt.bcrypt-cost=12

                    # Rate limiting
                    rate-limit.tokens-per-hour=100
                    rate-limit.bucket-type=token-bucket
                    """;
            Files.writeString(defaultConfig, config);
            System.out.println("  Created default config: " + defaultConfig);
        }
    }

    /**
     * Demonstrates: Files.readAllLines() + Stream processing.
     * Reads configuration as key-value pairs.
     */
    public Map<String, String> loadConfig() throws IOException {
        Path configFile = configDir.resolve("gateway.properties");

        if (!Files.exists(configFile)) {
            return Map.of();
        }

        // NIO2: Read all lines and process with streams
        return Files.readAllLines(configFile).stream()
                .filter(line -> !line.isBlank() && !line.startsWith("#"))
                .map(line -> line.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(java.util.stream.Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }

    /**
     * Demonstrates: Files.writeString() with StandardOpenOption.
     * Appends a new audit log entry to the log file.
     */
    public void appendAuditLog(String logEntry) throws IOException {
        Path logFile = logsDir.resolve("audit.log");

        // NIO2: Append to file, creating if needed
        Files.writeString(logFile, logEntry + System.lineSeparator(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    /**
     * Demonstrates: Files.walk() - recursive directory traversal.
     * Lists all files in the gateway directory with metadata.
     */
    public List<String> listAllFiles() throws IOException {
        List<String> fileInfoList = new ArrayList<>();

        // NIO2: walk() returns a Stream<Path> for lazy traversal (try-with-resources)
        try (Stream<Path> paths = Files.walk(configDir.getParent())) {
            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            // NIO2: Read file attributes
                            BasicFileAttributes attrs = Files.readAttributes(
                                    path, BasicFileAttributes.class);

                            String info = String.format("  %-40s  %,8d bytes  Modified: %s",
                                    configDir.getParent().relativize(path),
                                    attrs.size(),
                                    Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis())
                                            .atZone(ZoneId.systemDefault())
                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                            fileInfoList.add(info);
                        } catch (IOException e) {
                            fileInfoList.add("  Error reading: " + path);
                        }
                    });
        }
        return fileInfoList;
    }

    /**
     * Demonstrates: Files.readString() - reads entire file as a single String.
     */
    public String readFileContent(String filename) throws IOException {
        Path filePath = configDir.resolve(filename);
        if (Files.exists(filePath)) {
            return Files.readString(filePath);
        }
        return "File not found: " + filename;
    }

    /**
     * Demonstrates: Files.write() with byte array + Path.of().
     * Exports configuration as a JSON-like format.
     */
    public Path exportConfigAsJson() throws IOException {
        Map<String, String> config = loadConfig();
        StringBuilder json = new StringBuilder("{\n");

        config.forEach((key, value) ->
                json.append(String.format("  \"%s\": \"%s\",\n", key, value)));

        // Remove trailing comma
        if (json.length() > 2) {
            json.setLength(json.length() - 2);
            json.append("\n");
        }
        json.append("}\n");

        Path exportPath = logsDir.resolve("config-export.json");
        Files.write(exportPath, json.toString().getBytes());
        return exportPath;
    }

    public Path getConfigDir() { return configDir; }
    public Path getLogsDir() { return logsDir; }
}
