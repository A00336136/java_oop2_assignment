# OOP2 Assignment — Comprehensive Practice & Study Guide
## AI Gateway Administration Console — Java 25 Feature Demonstration
### MSc Software Design with Cloud Native Computing — TUS Midlands 2026

---

## TABLE OF CONTENTS

1.  [Project Overview & Architecture](#1-project-overview--architecture)
2.  [How to Compile & Run](#2-how-to-compile--run)
3.  [Feature 1: Records](#3-feature-1-records)
4.  [Feature 2: Sealed Classes & Interfaces](#4-feature-2-sealed-classes--interfaces)
5.  [Feature 3: Sorting with Comparator.comparing()](#5-feature-3-sorting)
6.  [Feature 4: Lambdas (Consumer, Predicate, Supplier, Function)](#6-feature-4-lambdas)
7.  [Feature 5: Streams — Terminal Operations](#7-feature-5-streams-terminal-operations)
8.  [Feature 6: Streams — Intermediate Operations](#8-feature-6-streams-intermediate-operations)
9.  [Feature 7: Collectors (toMap, groupingBy, partitioningBy)](#9-feature-7-collectors)
10. [Feature 8: Switch Expressions & Pattern Matching](#10-feature-8-switch-expressions--pattern-matching)
11. [Feature 9: Date/Time API](#11-feature-9-datetime-api)
12. [Feature 10: Concurrency (ExecutorService + Callable)](#12-feature-10-concurrency)
13. [Feature 11: NIO2 (Path, Files)](#13-feature-11-nio2)
14. [Feature 12: Localisation (ResourceBundle)](#14-feature-12-localisation)
15. [Feature 13: Java 25 — JEP 512 (Compact Source Files)](#15-feature-13-jep-512)
16. [Feature 14: Java 25 — JEP 513 (Flexible Constructor Bodies)](#16-feature-14-jep-513)
17. [Feature 15: Java 25 — Scoped Values](#17-feature-15-scoped-values)
18. [Feature 16: Java 25 — Stream Gatherers](#18-feature-16-stream-gatherers)
19. [Screencast Recording Strategy (10 min)](#19-screencast-recording-strategy)
20. [Report Writing Guide](#20-report-writing-guide)
21. [Evaluation Section Template](#21-evaluation-section-template)
22. [Potential Examiner Questions & Answers](#22-potential-examiner-questions--answers)
23. [Quick Revision Cheat Sheet](#23-quick-revision-cheat-sheet)

---

## 1. PROJECT OVERVIEW & ARCHITECTURE

### What is this application?
An **AI Gateway Administration Console** — a command-line tool that simulates managing
and monitoring the Secure AI Gateway (SAG), a real enterprise system that secures
LLM (Large Language Model) deployments.

### Why this domain?
The AI Gateway domain naturally requires:
- **Audit logs** (Records) — immutable request data
- **Security events** (Sealed Classes) — restricted event hierarchy
- **Parallel guardrail checks** (Concurrency) — NeMo, LlamaGuard, Presidio run simultaneously
- **PII detection** (Lambdas/Streams) — functional transformations on text
- **Configuration files** (NIO2) — reading/writing gateway settings
- **Multi-language support** (Localisation) — serving global enterprises

### Architecture (3-Layer)
```
┌─────────────────────────────────────────────────────────┐
│                    MAIN.java (Entry Point)               │
│              Orchestrates 15 demonstration sections       │
├──────────────┬──────────────────┬────────────────────────┤
│  MODEL       │  SERVICE          │  UTILITY                │
│              │                   │                          │
│ AuditLog     │ AuditService      │ PiiMasker               │
│   (record)   │  (Streams/Sort)   │  (Lambdas/Functions)    │
│              │                   │                          │
│ GatewayEvent │ GuardrailSim.     │ UserContext              │
│   (sealed)   │  (Concurrency)    │  (Scoped Values)        │
│              │                   │                          │
│ AuthEvent    │ ConfigService     │ ThreatWindowGatherer    │
│ GuardrailEvt │  (NIO2)           │  (Stream Gatherers)     │
│ PiiEvent     │                   │                          │
│              │ EventAnalyzer     │                          │
│ ThreatLevel  │  (Switch/DateTime)│                          │
│   (enum)     │                   │                          │
│              │ LocalisationSvc   │                          │
│              │  (ResourceBundle)  │                          │
└──────────────┴──────────────────┴────────────────────────┘
```

### File Summary
| File | Lines | Key Features |
|------|-------|-------------|
| Main.java | 617 | Entry point, all 15 demos |
| AuditLog.java | 59 | Record, compact constructor, Comparable |
| GatewayEvent.java | 26 | Sealed interface |
| AuthEvent.java | 67 | JEP 513, sealed permitted class |
| GuardrailEvent.java | 65 | Sealed permitted class, nested enum |
| PiiEvent.java | 57 | Sealed permitted class, nested record |
| ThreatLevel.java | 39 | Enum with behaviour |
| AuditService.java | 278 | Sorting, Streams, Collectors |
| GuardrailSimulator.java | 158 | ExecutorService, Callable, Future |
| ConfigService.java | 173 | NIO2 (Path, Files, walk) |
| EventAnalyzer.java | 140 | Switch expressions, Date/Time API |
| LocalisationService.java | 125 | ResourceBundle, Locale, MessageFormat |
| PiiMasker.java | 141 | Lambdas (all 6 types) |
| UserContext.java | 82 | Scoped Values (JEP 487) |
| ThreatWindowGatherer.java | 124 | Stream Gatherers (JEP 485) |
| SampleDataGenerator.java | 82 | Test data factory |

---

## 2. HOW TO COMPILE & RUN

### Prerequisites
- Java 25 (verify: `java --version` should show `java 25.x.x`)
- `--enable-preview` flag required for JEP 512, 513, Scoped Values, Gatherers

### Compile
```bash
cd ~/oop2
javac --enable-preview --source 25 -d target/classes \
    $(find src/main/java -name "*.java")
cp src/main/resources/*.properties target/classes/
```

### Run Main Application
```bash
java --enable-preview -cp target/classes com.gateway.Main
```

### Run JEP 512 Demo (Separate File)
```bash
java --enable-preview HelloGateway.java
```

### IMPORTANT for Screencast
When demoing, ALWAYS explain: *"I'm using `--enable-preview` because Java 25 features
like Scoped Values, Stream Gatherers, and Flexible Constructor Bodies are preview features
that require this flag to be enabled at both compile time and runtime."*

---

## 3. FEATURE 1: RECORDS

### File: `model/AuditLog.java`

### What is a Record?
A **Record** is a special class that is:
- **Immutable** — all fields are `final`
- **Transparent** — auto-generates `toString()`, `equals()`, `hashCode()`
- **Concise** — no boilerplate getters, constructor, etc.

### Key Code to Explain
```java
// This single line replaces ~80 lines of traditional Java class code:
public record AuditLog(
    String id,
    String username,
    String prompt,
    String response,
    LocalDateTime timestamp,
    Duration responseTime,
    boolean piiDetected,
    boolean guardrailBlocked,
    ThreatLevel threatLevel,
    String sourceIp
) implements Comparable<AuditLog> {
```

### What does the compiler auto-generate?
1. A **canonical constructor** with all parameters
2. **Accessor methods** (not getXxx — just `id()`, `username()`, etc.)
3. `toString()` that prints all fields
4. `equals()` that compares all fields
5. `hashCode()` based on all fields

### Compact Constructor
```java
public AuditLog {  // <-- NO parameter list! This is compact form.
    if (username == null || username.isBlank()) {
        throw new IllegalArgumentException("Username must not be blank");
    }
    // You can reassign parameters BEFORE they're stored:
    if (timestamp == null) {
        timestamp = LocalDateTime.now();  // default value
    }
}
```
**Key Point:** In a compact constructor, you validate/transform parameters. The actual
field assignment (`this.username = username`) happens AUTOMATICALLY after the body runs.

### Custom Methods on Records
```java
public boolean isFlagged() {
    return piiDetected || guardrailBlocked || threatLevel.severity() > 0;
}
```
Records CAN have custom methods. They just can't have mutable instance fields.

### Nested Record (in PiiEvent.java)
```java
public record PiiEntity(String type, String maskedValue, int startIndex, int endIndex) {}
```
Records can be nested inside other classes.

### Practice Q&A
**Q: Why use a Record instead of a class for AuditLog?**
A: Because audit logs are immutable data carriers — once created, they should never change.
Records enforce this immutability and eliminate boilerplate.

**Q: Can you add fields to a Record that aren't in the constructor?**
A: No. All instance fields must be declared in the Record header. You can only add
static fields and custom methods.

**Q: What's the difference between a compact and canonical constructor?**
A: Compact has no parameter list `public AuditLog { }` — it's syntactic sugar.
Canonical has the full parameter list `public AuditLog(String id, ...) { this.id = id; }`.

---

## 4. FEATURE 2: SEALED CLASSES & INTERFACES

### Files: `model/GatewayEvent.java`, `AuthEvent.java`, `GuardrailEvent.java`, `PiiEvent.java`

### What is a Sealed Interface?
A **sealed interface** restricts which classes can implement it using `permits`:
```java
public sealed interface GatewayEvent
        permits AuthEvent, GuardrailEvent, PiiEvent {
    String eventId();
    LocalDateTime occurredAt();
    String description();
}
```

### Why Sealed?
1. **Security** — No one can create a new GatewayEvent subtype without modifying the source
2. **Exhaustive Pattern Matching** — The compiler KNOWS all possible subtypes, so switch
   expressions don't need a `default` case
3. **Domain Modelling** — In our gateway, only 3 types of events exist: Auth, Guardrail, PII

### Permitted Subtypes Must Be:
- `final` (cannot be extended further) — like our 3 event classes
- `sealed` (further restricts its own subtypes)
- `non-sealed` (opens back up to extension)

### How Sealed Enables Pattern Matching
```java
// In EventAnalyzer.java — NO DEFAULT NEEDED because GatewayEvent is sealed!
return switch (event) {
    case AuthEvent auth     -> "Auth: " + auth.username();
    case GuardrailEvent g   -> "Guardrail: " + g.layer();
    case PiiEvent pii       -> "PII: " + pii.entitiesDetected().size();
    // No default! Compiler verifies exhaustiveness.
};
```

### Practice Q&A
**Q: What happens if you add a 4th class implementing GatewayEvent without adding it to permits?**
A: Compilation error: "class is not allowed to extend sealed interface GatewayEvent"

**Q: Why did you make AuthEvent, GuardrailEvent, PiiEvent all `final`?**
A: Because they are leaf classes in our sealed hierarchy. They represent concrete event
types that shouldn't be extended further.

**Q: What's the advantage over using instanceof checks?**
A: Sealed + switch gives compile-time exhaustiveness checking. If you add a new permitted
type and forget to handle it in a switch, the compiler will ERROR — catching bugs early.

---

## 5. FEATURE 3: SORTING

### File: `service/AuditService.java` (lines 30-65)

### Comparator.comparing()
```java
// Sort by timestamp (natural order)
logs.stream()
    .sorted(Comparator.comparing(AuditLog::timestamp))
    .toList();
```
`Comparator.comparing()` creates a Comparator from a key extraction function.
`AuditLog::timestamp` is a **method reference** — same as `log -> log.timestamp()`.

### .reversed()
```java
// Sort by response time, SLOWEST first
logs.stream()
    .sorted(Comparator.comparing(AuditLog::responseTime).reversed())
    .toList();
```

### .thenComparing() — Chained Sort
```java
// Primary: Threat level (highest first). Secondary: Timestamp (newest first)
logs.stream()
    .sorted(Comparator.comparing((AuditLog a) -> a.threatLevel().severity())
            .reversed()
            .thenComparing(AuditLog::timestamp, Comparator.reverseOrder()))
    .toList();
```
**Key Point:** `thenComparing()` is only used when the primary sort finds equal elements.

### Practice Q&A
**Q: What is `AuditLog::timestamp` called?**
A: A **method reference** — specifically an "instance method reference of an arbitrary object of a particular type." It's equivalent to the lambda `log -> log.timestamp()`.

**Q: Why use Comparator.comparing() instead of implementing Comparable everywhere?**
A: Comparator allows sorting by different criteria without modifying the class. You can
create multiple comparators for the same type (by timestamp, by response time, etc.).

---

## 6. FEATURE 4: LAMBDAS

### File: `util/PiiMasker.java`

### The 6 Functional Interfaces Used

| Interface | Signature | Purpose in Our App |
|-----------|-----------|-------------------|
| `Predicate<T>` | `T -> boolean` | Test if text contains PII |
| `Consumer<T>` | `T -> void` | Log PII detection results |
| `Function<T,R>` | `T -> R` | Transform/mask text |
| `Supplier<T>` | `() -> T` | Lazily retrieve PII types list |
| `UnaryOperator<T>` | `T -> T` (same type) | Individual masking rule |
| `BiFunction<T,U,R>` | `(T, U) -> R` | Create PiiEvent from two inputs |

### Predicate<String> — Testing a Condition
```java
public Predicate<String> containsPii() {
    return text -> rules.stream()
            .anyMatch(rule -> rule.pattern().matcher(text).find());
}

// Usage:
Predicate<String> hasPii = masker.containsPii();
hasPii.test("Hello world");          // false
hasPii.test("SSN: 123-45-6789");     // true
```

### Consumer<String> — Performing an Action (no return)
```java
public Consumer<String> logPiiDetection() {
    return text -> {
        rules.stream()
            .filter(rule -> rule.pattern().matcher(text).find())
            .forEach(rule -> System.out.printf("PII DETECTED [%s]%n", rule.name()));
    };
}

// Usage:
Consumer<String> logger = masker.logPiiDetection();
logger.accept("Contact john@test.com");  // Prints: PII DETECTED [EMAIL]
```

### Function<String, String> — Transformation with Composition
```java
public Function<String, String> buildMaskingPipeline() {
    Function<String, String> pipeline = Function.identity();  // start: input -> input
    for (PiiRule rule : rules) {
        Function<String, String> ruleFunction = input ->
            rule.pattern().matcher(input).replaceAll(match -> rule.masker().apply(match.group()));
        pipeline = pipeline.andThen(ruleFunction);  // CHAIN functions
    }
    return pipeline;
}
```
**Key Point:** `andThen()` chains functions: `f.andThen(g)` means "apply f first, then g."

### Supplier<List<String>> — Lazy Evaluation
```java
public Supplier<List<String>> supportedPiiTypes() {
    return () -> rules.stream().map(PiiRule::name).toList();
}

// Usage — the list is NOT created until .get() is called:
Supplier<List<String>> types = masker.supportedPiiTypes();
types.get();  // NOW the computation runs
```

### Practice Q&A
**Q: What's the difference between Function and UnaryOperator?**
A: `UnaryOperator<T>` is a specialisation of `Function<T,T>` where input and output
are the same type. We use `UnaryOperator<String>` for masking rules (String -> String).

**Q: Explain `Function.identity()`.**
A: Returns a function that returns its input unchanged: `x -> x`. We use it as the
starting point of our composition pipeline.

**Q: What's the difference between `andThen()` and `compose()`?**
A: `f.andThen(g)` = apply f first, then g. `f.compose(g)` = apply g first, then f.

---

## 7. FEATURE 5: STREAMS — TERMINAL OPERATIONS

### File: `service/AuditService.java` (lines 95-165)

### ALL 9 Terminal Operations Demonstrated

| Operation | Code | Returns |
|-----------|------|---------|
| `min()` | `logs.stream().min(Comparator.comparing(AuditLog::responseTime))` | `Optional<AuditLog>` |
| `max()` | `logs.stream().max(Comparator.comparing(AuditLog::responseTime))` | `Optional<AuditLog>` |
| `count()` | `logs.stream().filter(AuditLog::guardrailBlocked).count()` | `long` |
| `findFirst()` | `logs.stream().filter(AuditLog::piiDetected).findFirst()` | `Optional<AuditLog>` |
| `findAny()` | `logs.parallelStream().filter(...).findAny()` | `Optional<AuditLog>` |
| `allMatch()` | `logs.stream().allMatch(log -> !log.isFlagged())` | `boolean` |
| `anyMatch()` | `logs.stream().anyMatch(AuditLog::piiDetected)` | `boolean` |
| `noneMatch()` | `logs.stream().noneMatch(log -> log.threatLevel() == CRITICAL)` | `boolean` |
| `forEach()` | `logs.forEach(action)` | `void` |

### Key Differences
- `findFirst()` is **deterministic** — always returns first element in encounter order
- `findAny()` is **non-deterministic** in parallel streams — returns whichever element
  is found first by any thread (better performance in parallel)
- `allMatch/anyMatch/noneMatch` are **short-circuiting** — they stop processing as soon
  as the result is determined

### Practice Q&A
**Q: Why does min() return Optional<AuditLog> and not AuditLog?**
A: Because the stream might be empty. Optional forces the caller to handle the "no result" case.

**Q: When would you use findAny() over findFirst()?**
A: In **parallel streams** when you don't care about order and want maximum performance.
findFirst() in a parallel stream is slower because it must maintain encounter order.

**Q: What's the difference between count() and collect(Collectors.counting())?**
A: Functionally the same, but `count()` is a direct terminal operation and
`Collectors.counting()` is a downstream collector used inside `groupingBy()`.

---

## 8. FEATURE 6: STREAMS — INTERMEDIATE OPERATIONS

### File: `service/AuditService.java` (lines 167-210)

### ALL 5 Intermediate Operations

| Operation | What it Does |
|-----------|-------------|
| `filter(predicate)` | Keeps only elements matching the predicate |
| `map(function)` | Transforms each element to a new type |
| `distinct()` | Removes duplicates (uses equals/hashCode) |
| `sorted()` | Sorts elements (natural order or with Comparator) |
| `limit(n)` | Takes only the first n elements |

### Complex Pipeline Example
```java
public List<String> uniqueFlaggedIps() {
    return logs.stream()
        .filter(AuditLog::isFlagged)     // 1. Keep only flagged logs
        .map(AuditLog::sourceIp)          // 2. Extract IP address
        .distinct()                        // 3. Remove duplicate IPs
        .sorted()                          // 4. Sort alphabetically
        .toList();                         // 5. Collect to List (terminal)
}
```

### Key Concepts
- Intermediate operations are **LAZY** — they don't execute until a terminal operation runs
- They return a new Stream (allowing chaining)
- Order matters: `filter` before `map` reduces the number of transformations needed

### Practice Q&A
**Q: Are intermediate operations eager or lazy?**
A: **Lazy.** Nothing happens until a terminal operation (like `toList()`, `count()`, `forEach()`)
triggers the pipeline.

**Q: What happens if you call `.distinct()` on a stream of AuditLog records?**
A: It uses `equals()` and `hashCode()` — which Records auto-generate based on ALL fields.
So two AuditLog records are "distinct" unless every single field matches.

---

## 9. FEATURE 7: COLLECTORS

### File: `service/AuditService.java` (lines 212-260)

### Collectors.toMap()
```java
public Map<String, Long> requestCountPerUser() {
    return logs.stream()
        .collect(Collectors.toMap(
            AuditLog::username,    // key extractor
            log -> 1L,             // value mapper (each log = 1)
            Long::sum              // merge function for duplicate keys
        ));
}
```
**Key Point:** The 3rd argument (`Long::sum`) handles duplicate keys.
Without it, if two logs have the same username, you get `IllegalStateException`.

### Collectors.groupingBy()
```java
// Simple grouping
public Map<ThreatLevel, List<AuditLog>> groupByThreatLevel() {
    return logs.stream()
        .collect(Collectors.groupingBy(AuditLog::threatLevel));
}

// With downstream collector
public Map<LocalDate, Long> requestsPerDay() {
    return logs.stream()
        .collect(Collectors.groupingBy(
            log -> log.timestamp().toLocalDate(),  // classifier
            Collectors.counting()                   // downstream
        ));
}
```

### Collectors.partitioningBy()
```java
public Map<Boolean, List<AuditLog>> partitionByFlagged() {
    return logs.stream()
        .collect(Collectors.partitioningBy(AuditLog::isFlagged));
}
// Returns: { true: [flagged logs], false: [clean logs] }
```

### groupingBy vs partitioningBy
- `groupingBy` — groups by ANY classifier (any type as key)
- `partitioningBy` — special case: splits into exactly 2 groups (`true` and `false`)

### Practice Q&A
**Q: What's the difference between groupingBy and partitioningBy?**
A: `partitioningBy` always returns a `Map<Boolean, List<T>>` with exactly 2 entries
(true and false), even if one list is empty. `groupingBy` can have any number of groups.

**Q: What does the 3rd argument in toMap() do?**
A: It's the **merge function** — called when two elements map to the same key.
`Long::sum` adds the values together. Without it, duplicate keys cause an exception.

---

## 10. FEATURE 8: SWITCH EXPRESSIONS & PATTERN MATCHING

### File: `service/EventAnalyzer.java`

### Switch Expression (returns a value)
```java
// OLD switch statement (pre-Java 14):
String result;
switch (event.type()) {
    case "auth": result = "Auth event"; break;
    case "guard": result = "Guard event"; break;
    default: result = "Unknown"; break;
}

// NEW switch expression (Java 14+):
String result = switch (event.type()) {
    case "auth"  -> "Auth event";
    case "guard" -> "Guard event";
    default      -> "Unknown";
};  // <-- note the semicolon!
```

### Pattern Matching on Sealed Types
```java
public String analyzeEvent(GatewayEvent event) {
    return switch (event) {
        case AuthEvent auth when auth.isAuthenticated() ->   // guarded pattern
            "AUTH_SUCCESS: " + auth.username();

        case AuthEvent auth ->                                // unguarded fallback
            "AUTH_FAILURE: " + auth.failureReason();

        case GuardrailEvent guard when guard.isBlocked() ->
            "GUARDRAIL_BLOCK: " + guard.category();

        case GuardrailEvent guard ->
            "GUARDRAIL_PASS: cleared in " + guard.latencyMs() + "ms";

        case PiiEvent pii when pii.entitiesDetected().size() > 3 ->
            "PII_CRITICAL: " + pii.entitiesDetected().size() + " entities";

        case PiiEvent pii ->
            "PII_DETECTED: " + pii.entitiesDetected().size() + " entities";
    };  // NO DEFAULT — sealed interface guarantees exhaustiveness!
}
```

### Guarded Patterns (`when` clause)
The `when` keyword adds a condition to a pattern:
- `case AuthEvent auth when auth.isAuthenticated()` — matches ONLY authenticated events
- `case AuthEvent auth` — matches all remaining AuthEvents (must come AFTER guarded ones)

### Practice Q&A
**Q: Why is there no `default` case in the switch?**
A: Because `GatewayEvent` is a **sealed interface** with exactly 3 permitted types.
The compiler verifies all 3 are handled. If you add a 4th type and forget to handle it,
you get a compile error.

**Q: What is the `when` keyword called?**
A: A **guarded pattern.** It adds a boolean condition to a pattern match case.

**Q: What happens if you put `case AuthEvent auth` BEFORE `case AuthEvent auth when auth.isAuthenticated()`?**
A: The unguarded pattern would match ALL AuthEvents, making the guarded pattern unreachable.
The compiler would report a **dominance error.**

---

## 11. FEATURE 9: DATE/TIME API

### File: `service/EventAnalyzer.java` (lines 100-140)

### Key Classes Used
| Class | Purpose |
|-------|---------|
| `LocalDateTime` | Date + time without timezone (e.g., 2026-03-25T10:30:00) |
| `Duration` | Time-based amount (e.g., 85ms, 2h30m) |
| `DateTimeFormatter` | Custom date/time formatting |
| `LocalDate` | Date only, no time |

### Key Operations
```java
// Create
LocalDateTime now = LocalDateTime.now();
LocalDateTime yesterday = LocalDateTime.now().minusHours(24);

// Duration between two timestamps
Duration duration = Duration.between(from, to);
long hours = duration.toHours();
long minutes = duration.toMinutesPart();

// Custom formatting
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");
String formatted = now.format(formatter);  // "25 Mar 2026 10:30:00"

// Duration arithmetic
Duration avg = Duration.ofMillis(totalMillis / count);
```

### Practice Q&A
**Q: What's the difference between LocalDateTime and ZonedDateTime?**
A: `LocalDateTime` has no timezone info — just a date and time. `ZonedDateTime` includes
a timezone (like Europe/Dublin). For server-side logging, `LocalDateTime` is often sufficient.

**Q: Why use Duration instead of just storing milliseconds as a long?**
A: Duration is type-safe and self-documenting. You can't accidentally confuse it with
a count, an ID, or any other `long` value. It also has convenience methods like
`toHours()`, `toMinutesPart()`, `toMillis()`.

---

## 12. FEATURE 10: CONCURRENCY

### File: `service/GuardrailSimulator.java`

### Architecture
```
ExecutorService (3 threads)
    ├── Thread-1: evaluateNemo(prompt)      → GuardrailEvent
    ├── Thread-2: evaluateLlamaGuard(prompt) → GuardrailEvent
    └── Thread-3: evaluatePresidio(prompt)   → GuardrailEvent
         ↓ All run in PARALLEL ↓
    invokeAll() blocks until all 3 complete
         ↓
    Collect 3 Future<GuardrailEvent> results
```

### Key Concepts
```java
// 1. CREATE: Fixed thread pool
ExecutorService executorService = Executors.newFixedThreadPool(3);

// 2. DEFINE: List of Callable<GuardrailEvent> tasks
List<Callable<GuardrailEvent>> tasks = List.of(
    () -> evaluateNemo(prompt),        // Callable returns a value
    () -> evaluateLlamaGuard(prompt),
    () -> evaluatePresidio(prompt)
);

// 3. EXECUTE: invokeAll runs all tasks in parallel
List<Future<GuardrailEvent>> futures = executorService.invokeAll(tasks);

// 4. COLLECT: Get results from each Future
for (Future<GuardrailEvent> future : futures) {
    results.add(future.get());  // blocks until this task is done
}

// 5. SHUTDOWN: Always clean up!
executorService.shutdown();
```

### Callable vs Runnable
| Feature | Runnable | Callable |
|---------|----------|----------|
| Return value? | No (void) | Yes (any type) |
| Throws checked exceptions? | No | Yes |
| Used with | execute() | submit(), invokeAll() |

### Practice Q&A
**Q: Why use a fixed thread pool of 3?**
A: Because we have exactly 3 guardrail layers (NeMo, LlamaGuard, Presidio). Each gets
its own thread for true parallel execution.

**Q: What does `future.get()` do?**
A: It **blocks** the current thread until the Callable completes and returns its result.
If the Callable threw an exception, `.get()` wraps it in an `ExecutionException`.

**Q: Why is proper shutdown important?**
A: Without shutdown, threads stay alive and the JVM won't exit. We use `shutdown()` to
stop accepting new tasks, then `awaitTermination()` to wait for running tasks to finish.

**Q: What's the difference between submit() and invokeAll()?**
A: `submit()` starts ONE task asynchronously (non-blocking). `invokeAll()` starts ALL
tasks and blocks until every one completes.

---

## 13. FEATURE 11: NIO2

### File: `service/ConfigService.java`

### Key NIO2 APIs Used
| API | Purpose |
|-----|---------|
| `Path.of()` / `Path.resolve()` | Create/navigate file paths |
| `Files.createDirectories()` | Create nested directories (like mkdir -p) |
| `Files.writeString()` | Write text to a file |
| `Files.readAllLines()` | Read all lines as List<String> |
| `Files.exists()` | Check if file exists |
| `Files.walk()` | Recursive directory traversal (returns Stream<Path>) |
| `Files.readAttributes()` | Get file metadata (size, timestamps) |
| `StandardOpenOption.APPEND` | Append to file instead of overwrite |

### Key Code Patterns
```java
// Create directories
Path configDir = baseDir.resolve("config");
Files.createDirectories(configDir);  // creates parent dirs too

// Write string to file
Files.writeString(configFile, content);

// Append to file
Files.writeString(logFile, logEntry + System.lineSeparator(),
    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

// Read all lines and process with streams
Map<String, String> config = Files.readAllLines(configFile).stream()
    .filter(line -> !line.isBlank() && !line.startsWith("#"))
    .map(line -> line.split("=", 2))
    .collect(Collectors.toMap(p -> p[0].trim(), p -> p[1].trim()));

// Walk directory (try-with-resources — Stream must be closed!)
try (Stream<Path> paths = Files.walk(baseDir)) {
    paths.filter(Files::isRegularFile)
         .forEach(path -> System.out.println(path));
}
```

### Practice Q&A
**Q: Why use `try-with-resources` with `Files.walk()`?**
A: `Files.walk()` returns a `Stream<Path>` backed by open file handles. If you don't
close it, you leak file handles. `try-with-resources` auto-closes it.

**Q: What's the difference between `Files.write()` and `Files.writeString()`?**
A: `writeString()` takes a `CharSequence` (like String). `write()` takes `byte[]` or
`Iterable<String>`. `writeString()` is more convenient for text.

**Q: Why use NIO2 instead of the old java.io.File API?**
A: NIO2 is modern, more powerful, and more consistent. Key advantages:
better exception handling, symbolic link support, file attributes API, and
`Files.walk()` for stream-based directory traversal.

---

## 14. FEATURE 12: LOCALISATION

### File: `service/LocalisationService.java` + 3 properties files

### Key Concepts
```java
// 1. Load a ResourceBundle for a locale
Locale irish = Locale.of("ga", "IE");
ResourceBundle bundle = ResourceBundle.getBundle("messages", irish);
// Loads: messages_ga.properties (falls back to messages_en.properties)

// 2. Get a message
String welcome = bundle.getString("app.welcome");
// Returns: "Failte go dti Consol Riarachain an Gheata AI Slan"

// 3. Parameterised messages with MessageFormat
String pattern = bundle.getString("security.auth.success");
// Pattern: "Authentication successful for user: {0}"
String msg = MessageFormat.format(pattern, "ashaik");
// Returns: "Authentication successful for user: ashaik"

// 4. Locale-aware number formatting
NumberFormat nf = NumberFormat.getNumberInstance(Locale.FRENCH);
nf.format(15420);  // "15 420" (French uses space as thousands separator)

// 5. Locale-aware date formatting
DateTimeFormatter formatter = DateTimeFormatter
    .ofLocalizedDateTime(FormatStyle.MEDIUM)
    .withLocale(Locale.FRENCH);
// "25 mars 2026, 10:30:00" vs English "Mar 25, 2026, 10:30:00 AM"
```

### Our 3 Locales
| Locale | File | Language |
|--------|------|----------|
| `en` (English) | messages_en.properties | Default |
| `ga-IE` (Irish) | messages_ga.properties | Gaeilge |
| `fr` (French) | messages_fr.properties | Francais |

### Practice Q&A
**Q: What happens if a key is missing from the Irish properties file?**
A: ResourceBundle has a **fallback chain**: `messages_ga_IE.properties` -> `messages_ga.properties`
-> `messages.properties` (default). If not found in any, `MissingResourceException` is thrown.

**Q: Why use MessageFormat instead of String concatenation?**
A: MessageFormat supports locale-aware formatting of numbers, dates, and currencies within
the message pattern. Also, translators can reorder `{0}`, `{1}` parameters for different languages.

---

## 15. FEATURE 13: JEP 512 — COMPACT SOURCE FILES

### File: `HelloGateway.java` (root directory)

### What JEP 512 Does
Before Java 25:
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```

After JEP 512 (Java 25 Preview):
```java
void main() {
    System.out.println("Hello World");
}
```

### Key Changes:
1. **No class declaration needed** — the file becomes an "implicit class"
2. **Instance main()** — just `void main()` instead of `public static void main(String[])`
3. **No public required** — simplified access
4. **Auto-imports** — `java.io.*` and `java.util.*` are automatically available

### How to Run
```bash
java --enable-preview HelloGateway.java
```
Note: `--enable-preview` is required because JEP 512 is a preview feature in Java 25.

### Why We Have Both
- `HelloGateway.java` — demonstrates JEP 512 as a standalone script
- `Main.java` — uses standard `public static void main(String[] args)` because
  it's in a **package** (`com.gateway`) and packaged projects need the traditional form

### Practice Q&A
**Q: Can you use JEP 512 in a packaged project (with `package` statement)?**
A: No. Implicit classes (JEP 512) cannot have a package declaration. They're designed for
simple programs, scripts, and learning. Production code uses standard class declarations.

**Q: What is an "implicit class"?**
A: When you write `void main() { }` without a class declaration, the compiler creates
an implicit unnamed class for you. The file IS the class.

---

## 16. FEATURE 14: JEP 513 — FLEXIBLE CONSTRUCTOR BODIES

### File: `model/AuthEvent.java`

### The Problem (Pre-Java 25)
In older Java, you couldn't put statements before `super()` or `this()`:
```java
// OLD — This was ILLEGAL:
public Child(String name) {
    if (name == null) throw new IllegalArgumentException();  // COMPILE ERROR
    super(name);
}
```

### JEP 513 Solution
```java
// NOW — This is LEGAL in Java 25:
public AuthEvent(String eventId, String username, ...) {
    // Validation BEFORE field assignments
    if (eventId == null || eventId.isBlank()) {
        throw new IllegalArgumentException("Event ID cannot be blank");
    }
    if (username == null || username.isBlank()) {
        throw new IllegalArgumentException("Username cannot be blank");
    }
    // Now assign fields
    this.eventId = eventId;
    this.username = username;
    // ...
}
```

### Why This Matters
- **Fail-fast validation** — reject bad input before any object state is created
- **Security** — in our gateway, we ensure no AuthEvent can exist with blank credentials
- **Cleaner code** — no need for separate factory methods just for validation

### How We Demo It
```java
// Valid construction — works fine
new AuthEvent("EVT-100", "ashaik", true, "JWT-XYZ", null);

// Invalid — throws BEFORE fields are assigned
new AuthEvent("", "ashaik", true, null, null);
// throws: "Event ID cannot be blank"
```

### Practice Q&A
**Q: Before JEP 513, how would you validate constructor args?**
A: Either use a static factory method that validates first then calls `new`, or validate
after `super()` and throw — but by then, the partially constructed object might leak.

**Q: Does JEP 513 apply to Record compact constructors too?**
A: Records already allowed pre-validation in compact constructors. JEP 513 extends this
to regular class constructors with `super()` or `this()` calls.

---

## 17. FEATURE 15: SCOPED VALUES

### File: `util/UserContext.java`

### What Are Scoped Values?
Like `ThreadLocal`, but **better**:

| Feature | ThreadLocal | ScopedValue |
|---------|-------------|-------------|
| Mutability | Mutable (set/get) | Immutable within scope |
| Cleanup | Manual (remove()) | Automatic when scope exits |
| Child threads | Not inherited by default | Inherited (structured concurrency) |
| Performance | Slower (hash lookup) | Faster (no hashing) |

### How They Work
```java
// 1. DECLARE a ScopedValue (like a ThreadLocal variable)
public static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();

// 2. BIND it in a scope
ScopedValue.where(CURRENT_USER, "ashaik")
    .run(() -> {
        // Inside this lambda, CURRENT_USER.get() returns "ashaik"
        System.out.println(CURRENT_USER.get());  // "ashaik"

        // Nested rebinding
        ScopedValue.where(CURRENT_USER, "admin")
            .run(() -> {
                System.out.println(CURRENT_USER.get());  // "admin"
            });

        // Back to outer scope
        System.out.println(CURRENT_USER.get());  // "ashaik" (restored!)
    });

// 3. OUTSIDE scope — not bound
CURRENT_USER.isBound();  // false
CURRENT_USER.get();       // throws NoSuchElementException!
```

### Why Use in AI Gateway?
When a request arrives, we bind the authenticated user's identity:
```
Request arrives -> JWT validated -> UserContext bound
    -> Guardrail check (reads UserContext for audit)
    -> PII check (reads UserContext for logging)
    -> LLM call
    -> Audit log (reads UserContext for username)
Request ends -> ScopedValue automatically unbound
```
No risk of leaking user context to the next request!

### Practice Q&A
**Q: What happens if you call `.get()` outside a scope?**
A: `NoSuchElementException`. That's why we use `.isBound()` to check first, with a
fallback: `CURRENT_USER.isBound() ? CURRENT_USER.get() : "anonymous"`

**Q: Can you change a ScopedValue inside its scope?**
A: No. ScopedValues are **immutable within their scope.** You can only rebind in a new
nested scope using `.where().run()`. The outer scope's binding is preserved.

---

## 18. FEATURE 16: STREAM GATHERERS

### File: `util/ThreatWindowGatherer.java`

### What Are Gatherers?
Stream Gatherers are **custom intermediate operations** for streams.

Before Gatherers, you had: `filter`, `map`, `flatMap`, `distinct`, `sorted`, `limit`, `peek`.
But what if you need: sliding windows, batching, deduplication with a key, rate limiting?
You had to collect to a list and process manually. Gatherers solve this.

### Gatherer Components
```
Gatherer.ofSequential(initializer, integrator, finisher)
    │                    │              │
    │                    │              └─ Called after last element (emit remaining)
    │                    └─ Called for EACH element (main logic)
    └─ Creates initial state (like a buffer)
```

### Our Sliding Window Gatherer
```java
Gatherer.ofSequential(
    // INITIALIZER: Create the window buffer
    () -> new ArrayDeque<AuditLog>(windowSize),

    // INTEGRATOR: Process each element
    Gatherer.Integrator.ofGreedy((window, element, downstream) -> {
        window.addLast(element);                    // add to window
        if (window.size() > windowSize)
            window.removeFirst();                    // maintain window size

        if (window.size() == windowSize) {
            long flagged = window.stream()
                .filter(AuditLog::isFlagged).count();
            if (flagged >= threshold) {
                downstream.push(new ThreatWindow(...)); // EMIT alert!
            }
        }
        return true;  // continue processing
    })
);
```

### Usage
```java
// Sliding window: if 2+ flagged in any window of 5, emit alert
List<ThreatWindow> alerts = logs.stream()
    .gather(ThreatWindowGatherer.slidingThreatWindow(5, 2))
    .toList();

// Batch: group into lists of 4
List<List<AuditLog>> batches = logs.stream()
    .gather(ThreatWindowGatherer.batchOf(4))
    .toList();
```

### Practice Q&A
**Q: Why use `Gatherer.Integrator.ofGreedy()`?**
A: "Greedy" means the integrator processes ALL elements — it never short-circuits.
Use `ofGreedy` when you need to see every element (like our sliding window).

**Q: What's the difference between Gatherer and Collector?**
A: **Collector** is a terminal operation (at the end of a pipeline).
**Gatherer** is an intermediate operation (in the MIDDLE of a pipeline).
Gatherers can emit 0, 1, or many elements per input — they're more flexible.

**Q: When does the finisher run?**
A: After the last element is processed. It's used to emit any remaining buffered data.
Our `batchOf()` gatherer uses it to emit the last partial batch.

---

## 19. SCREENCAST RECORDING STRATEGY (10 MIN)

### CRITICAL: Do NOT exceed 10 minutes! (Penalties apply)

### Recommended Recording Tool
- **OBS Studio** (free) with screen recording + webcam overlay
- Or **QuickTime Player** on Mac (Screen Recording)
- Use a **good microphone** — audio quality is a marking criterion

### Annotation Tools
- **OBS Studio** has annotation plugins
- **Snagit** or **Annotate** apps for Mac
- Or use **IntelliJ IDEA's presentation mode** with cursor highlighting

### Timing Plan (Strict 10 Minutes)

| Time | Section | Show | Say |
|------|---------|------|-----|
| 0:00-0:30 | **Intro** | Project title slide or README | "This is an AI Gateway Admin Console demonstrating all OOP2 features" |
| 0:30-0:50 | **Compile & Run** | Terminal: compile command | "Using --enable-preview for Java 25 preview features" |
| 0:50-1:30 | **Records** | AuditLog.java + output | "Record auto-generates constructor, toString, equals. Compact constructor validates input." |
| 1:30-2:10 | **Sealed Classes** | GatewayEvent.java + 3 subtypes | "Sealed restricts implementations. Enables exhaustive switch — no default needed." |
| 2:10-3:00 | **Sorting** | AuditService.java + output | "Comparator.comparing with method reference, reversed(), thenComparing() for chaining." |
| 3:00-3:50 | **Lambdas** | PiiMasker.java + output | "Predicate tests, Consumer acts, Function transforms with andThen composition." |
| 3:50-4:30 | **Streams Terminal** | AuditService.java + output | "9 terminal ops: min, max, count, findFirst, findAny, allMatch, anyMatch, noneMatch, forEach." |
| 4:30-5:00 | **Streams Intermediate** | Output | "filter, map, distinct, sorted, limit — all lazy until terminal op." |
| 5:00-5:30 | **Collectors** | Output | "toMap with merge function, groupingBy with downstream counting, partitioningBy splits true/false." |
| 5:30-6:00 | **Switch + Pattern Matching** | EventAnalyzer.java | "Pattern matching on sealed interface — when guards for conditional matching." |
| 6:00-6:20 | **Date/Time** | Output | "Duration.between, DateTimeFormatter, toHours/toMinutesPart." |
| 6:20-7:00 | **Concurrency** | GuardrailSimulator.java + output | "3 Callables in ExecutorService, invokeAll runs parallel, future.get blocks for result." |
| 7:00-7:30 | **NIO2** | ConfigService.java + output | "Files.walk with try-with-resources, readAllLines to stream, writeString with APPEND." |
| 7:30-8:00 | **Localisation** | Output (EN/GA/FR) | "ResourceBundle loads locale-specific properties, MessageFormat for parameterised messages." |
| 8:00-8:30 | **Scoped Values** | UserContext.java + output | "ScopedValue replaces ThreadLocal — immutable, auto-cleanup, nested rebinding." |
| 8:30-9:00 | **Stream Gatherers** | ThreatWindowGatherer.java + output | "Custom intermediate op with initializer, integrator, finisher. Sliding window for threat detection." |
| 9:00-9:20 | **JEP 512 + 513** | HelloGateway.java + AuthEvent.java | "Compact source: no class declaration. Flexible constructor: validate before field assignment." |
| 9:20-10:00 | **Summary** | Feature checklist | "All fundamentals, advanced, and Java 25 features demonstrated. Thank you." |

### Annotation Tips (MARKS FOR THIS)
- Draw a **red rectangle** around key code when explaining
- Use **arrows** pointing to important lines
- Keep cursor **still** when not moving — don't wave it around
- **Zoom into** code sections (Ctrl+Mouse wheel in IDE)

---

## 20. REPORT WRITING GUIDE

### Structure (Keep it BRIEF — 3-4 pages max)

**Page 1: Introduction**
- Application: AI Gateway Administration Console
- Domain: Simulates managing an enterprise LLM security gateway
- Tech: Java 25, Maven, --enable-preview

**Page 2: User Stories / Features Covered**

| # | User Story | Java Feature | File |
|---|-----------|-------------|------|
| 1 | As a user, I want audit logs to be immutable data | Records | AuditLog.java |
| 2 | As a user, I want a restricted event type hierarchy | Sealed Classes | GatewayEvent.java |
| 3 | As a user, I want logs sorted by threat/time/speed | Sorting (Comparator) | AuditService.java |
| 4 | As a user, I want PII detected using functional rules | Lambdas (all 6 types) | PiiMasker.java |
| 5 | As a user, I want analytics on audit logs | Streams (9 terminal ops) | AuditService.java |
| 6 | As a user, I want filtered/transformed log views | Streams (5 intermediate) | AuditService.java |
| 7 | As a user, I want grouped statistics | Collectors (3 types) | AuditService.java |
| 8 | As a user, I want events classified by type | Switch + Pattern Matching | EventAnalyzer.java |
| 9 | As a user, I want time-based analytics | Date/Time API | EventAnalyzer.java |
| 10 | As a user, I want parallel guardrail evaluation | Concurrency | GuardrailSimulator.java |
| 11 | As a user, I want file-based config management | NIO2 | ConfigService.java |
| 12 | As a user, I want multi-language support | Localisation | LocalisationService.java |
| 13 | As a user, I want a simple gateway launcher | JEP 512 | HelloGateway.java |
| 14 | As a user, I want validated event construction | JEP 513 | AuthEvent.java |
| 15 | As a user, I want secure user context in threads | Scoped Values | UserContext.java |
| 16 | As a user, I want threat burst detection | Stream Gatherers | ThreatWindowGatherer.java |

**Page 3-4: Evaluation** (see next section)

---

## 21. EVALUATION SECTION TEMPLATE

This is worth significant marks. Be **honest and reflective.**

```
EVALUATION

Coverage Assessment:
I believe this project covers ALL features specified in the brief:

FUNDAMENTALS (All covered):
- Sorting: Comparator.comparing(), reversed(), thenComparing() — AuditService.java
- Lambdas: All 6 functional interfaces demonstrated in PiiMasker.java
- Streams Terminal: All 9 operations (min, max, count, findFirst, findAny,
  allMatch, anyMatch, noneMatch, forEach) — AuditService.java
- Streams Intermediate: filter, distinct, limit, map, sorted — AuditService.java
- Collectors: toMap (with merge), groupingBy (with downstream), partitioningBy
- Switch Expressions: Pattern matching on sealed types with guarded patterns
- Sealed Classes: GatewayEvent sealed interface with 3 final permitted classes
- Date/Time API: LocalDateTime, Duration, DateTimeFormatter
- Records: AuditLog (with compact constructor), PiiEntity (nested record)

ADVANCED (All covered):
- Concurrency: ExecutorService with 3-thread pool, Callable<GuardrailEvent>,
  invokeAll() for parallel execution, Future.get() for results
- NIO2: Path, Files.walk(), readAllLines(), writeString(), readAttributes()
- Localisation: ResourceBundle (3 locales), MessageFormat, NumberFormat

JAVA 25 (All 4 covered):
- JEP 512: HelloGateway.java with instance main() and implicit class
- JEP 513: AuthEvent constructor validates before field assignment
- Scoped Values: UserContext with where().run(), nested rebinding, isBound()
- Stream Gatherers: slidingThreatWindow and batchOf custom gatherers

Problems Encountered:
1. Stream Gatherers required explicit type parameters to avoid inference errors
   with List.copyOf() — resolved by using explicit generic types on ofSequential().
2. ScopedValue.call() in Java 25 uses CallableOp instead of java.util.concurrent.Callable
   — resolved by using the correct ScopedValue.CallableOp type.
3. JEP 512 implicit classes cannot use println() directly in Java 25.0.1 —
   used System.out.println() instead.

What I Would Improve:
- Add a persistence layer (database) for audit logs
- Implement a TUI (text user interface) with menus
- Add unit tests with JUnit 5
- Use Virtual Threads (Project Loom) for the concurrency section
```

---

## 22. POTENTIAL EXAMINER QUESTIONS & ANSWERS

### General Architecture Questions

**Q: Why did you choose the AI Gateway domain?**
A: It naturally maps to all required Java features: immutable audit logs fit Records,
restricted event types fit Sealed classes, parallel guardrail evaluation fits Concurrency,
PII detection fits functional Lambdas and Streams, and multi-language enterprise support
fits Localisation.

**Q: How is your code organised?**
A: Three-layer architecture: Model (data classes), Service (business logic), Utility
(cross-cutting concerns). Main.java orchestrates 15 demonstration sections.

### Deep Dive Questions

**Q: Walk me through how the PII masking pipeline works.**
A: PiiMasker has a list of PiiRules, each containing a regex Pattern and an
UnaryOperator masker. buildMaskingPipeline() chains all rules using Function.andThen()
starting from Function.identity(). When applied to text, each rule's regex runs
sequentially, replacing matches with masked values like [EMAIL_REDACTED].

**Q: Explain how parallel guardrail evaluation works.**
A: GuardrailSimulator creates an ExecutorService with 3 fixed threads. Three Callable
tasks are created (one per guardrail layer). invokeAll() dispatches all 3 to the thread
pool simultaneously. Each thread simulates evaluation with Thread.sleep() and returns
a GuardrailEvent. The main thread collects results via future.get() which blocks until
each task completes. If ANY guardrail blocks, the request is rejected (fail-closed).

**Q: How does the sliding window gatherer detect threat bursts?**
A: It maintains an ArrayDeque as a sliding buffer. For each new AuditLog, it adds to
the buffer and removes the oldest if exceeding window size. When the window is full,
it counts flagged entries. If the count meets the threshold, it creates a ThreatWindow
record and pushes it downstream. The gatherer is stateful — it remembers the window
between elements.

**Q: How do Scoped Values differ from ThreadLocal in practice?**
A: Three key differences: (1) ScopedValues are immutable within their scope — you can't
accidentally overwrite them. (2) They're automatically cleaned up when the scope exits —
no memory leak risk. (3) They're inherited by child threads in structured concurrency.
In our gateway, this means the authenticated user identity is safely propagated through
the entire request pipeline without risk of leaking to the next request.

### Java 25 Specific Questions

**Q: Why does --enable-preview need to be used?**
A: Because JEP 512 (Compact Source Files), JEP 513 (Flexible Constructor Bodies),
Scoped Values, and Stream Gatherers are **preview features** in Java 25. Preview features
are fully functional but may change in future releases. The flag must be passed at both
compile time (`javac --enable-preview`) and runtime (`java --enable-preview`).

**Q: Could you use the Gatherer API without --enable-preview?**
A: In Java 25, no — Stream Gatherers (JEP 485) is a preview feature. It may become a
standard feature in a future Java release, at which point --enable-preview would no
longer be needed.

---

## 23. QUICK REVISION CHEAT SHEET

### Records
```java
record Foo(int x, String y) {}           // Auto: constructor, getters, equals, hashCode, toString
record Foo(int x) { public Foo { /*validate*/ } }  // Compact constructor
```

### Sealed
```java
sealed interface I permits A, B {}
final class A implements I {}
final class B implements I {}
// switch(i) { case A a -> ...; case B b -> ...; }  // No default needed!
```

### Sorting
```java
Comparator.comparing(Foo::field)                    // ascending
Comparator.comparing(Foo::field).reversed()         // descending
Comparator.comparing(Foo::f1).thenComparing(Foo::f2) // chained
```

### Lambdas
```java
Predicate<T>:       T -> boolean      test()
Consumer<T>:        T -> void         accept()
Function<T,R>:      T -> R            apply()
Supplier<T>:        () -> T           get()
UnaryOperator<T>:   T -> T            apply()
BiFunction<T,U,R>:  (T, U) -> R       apply()
```

### Streams Terminal
```java
min(comparator) -> Optional      max(comparator) -> Optional
count() -> long                  findFirst() -> Optional
findAny() -> Optional            allMatch(pred) -> boolean
anyMatch(pred) -> boolean        noneMatch(pred) -> boolean
forEach(consumer) -> void        collect(collector) -> R
```

### Streams Intermediate
```java
filter(pred)    map(func)    distinct()    sorted()    limit(n)
```

### Collectors
```java
Collectors.toMap(keyFunc, valFunc, mergeFunc)
Collectors.groupingBy(classifier)
Collectors.groupingBy(classifier, downstream)
Collectors.partitioningBy(predicate)
```

### Switch + Pattern Matching
```java
return switch (event) {
    case Type t when t.condition() -> "guarded";
    case Type t                    -> "unguarded";
};
```

### Date/Time
```java
LocalDateTime.now()    Duration.between(a, b)    Duration.ofMillis(n)
DateTimeFormatter.ofPattern("dd MMM yyyy")
```

### Concurrency
```java
ExecutorService es = Executors.newFixedThreadPool(n);
List<Future<T>> futures = es.invokeAll(callables);
T result = future.get();
es.shutdown();
```

### NIO2
```java
Path p = Path.of("dir", "file");    Files.createDirectories(p);
Files.writeString(p, text);         Files.readAllLines(p);
Files.walk(dir)                     Files.readAttributes(p, BasicFileAttributes.class);
```

### Localisation
```java
Locale locale = Locale.of("ga", "IE");
ResourceBundle rb = ResourceBundle.getBundle("messages", locale);
rb.getString("key");
MessageFormat.format(pattern, args);
```

### Java 25
```java
// JEP 512: void main() { } in file without class declaration
// JEP 513: Statements before super()/this() in constructors
// Scoped Values:
ScopedValue.where(SV, value).run(() -> { SV.get(); });
// Gatherers:
stream.gather(Gatherer.ofSequential(init, integrator, finisher))
```

---

## FINAL ADVICE

1. **Practice running the app 3-4 times** before recording
2. **Rehearse your 10-min script** with a timer
3. **Know EVERY line of code** — they may ask "what does line X do?"
4. **Be ready to explain WHY** — not just what, but WHY you chose each approach
5. **Record audio in a quiet room** — close windows, silence notifications
6. **Save your recording in .mp4 format** and test playback before submitting
7. **Submit early** — don't wait until 10:59pm on April 19th!

Good luck! You've got every feature covered. Study this guide, understand the code,
and deliver a confident, well-annotated screencast. 🎯
