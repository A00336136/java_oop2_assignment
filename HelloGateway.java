/**
 * JEP 512 - Compact Source File & Instance Main Method (Java 25 Preview).
 *
 * This file demonstrates the simplified entry point syntax:
 *   - No class declaration needed (implicit class)
 *   - Instance main() instead of public static void main(String[] args)
 *   - Automatically imports java.io.* and java.util.*
 *
 * To run: java --enable-preview HelloGateway.java
 *
 * NOTE: This is a standalone demo file separate from the main project.
 *       The main project uses standard packaging (com.gateway.Main).
 */
void main() {
    System.out.println("╔═══════════════════════════════════════════╗");
    System.out.println("║  Hello from Secure AI Gateway!            ║");
    System.out.println("║  JEP 512: Compact Source Files            ║");
    System.out.println("║  Java 25 Preview Feature                  ║");
    System.out.println("╚═══════════════════════════════════════════╝");

    // No import needed for java.util classes in implicit class
    var features = java.util.List.of(
        "Records",
        "Sealed Classes",
        "Stream Gatherers",
        "Scoped Values",
        "Flexible Constructors"
    );

    System.out.println("\nJava 25 features in this project:");
    for (int i = 0; i < features.size(); i++) {
        System.out.println("  %d. %s".formatted(i + 1, features.get(i)));
    }

    System.out.println("\nGateway Status: HEALTHY");
    System.out.println("Guardrails: NeMo + LlamaGuard + Presidio = 3-layer defence");
}
