package com.gateway.util;

/**
 * UserContext - Thread-safe user context using Java 25 Scoped Values.
 *
 * Demonstrates:
 *   - JAVA 25 - SCOPED VALUES (JEP 487): Thread-local-like values that are
 *     immutable, inherited by child threads, and automatically cleaned up.
 *     Scoped Values replace ThreadLocal for many use cases, offering better
 *     performance and preventing accidental leaks.
 *
 * In SAG, this would hold the authenticated user's identity across the
 * request processing pipeline (guardrails -> PII -> audit logging).
 */
public class UserContext {

    /**
     * SCOPED VALUE: Holds the current authenticated username.
     * Unlike ThreadLocal, ScopedValue is:
     *   1. Immutable within its scope (cannot be changed, only rebound)
     *   2. Automatically cleaned up when the scope exits
     *   3. Inherited by child threads (structured concurrency)
     */
    public static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();

    /**
     * SCOPED VALUE: Holds the current request's JWT token ID for audit trail.
     */
    public static final ScopedValue<String> CURRENT_JWT_ID = ScopedValue.newInstance();

    /**
     * SCOPED VALUE: Holds the request correlation ID for distributed tracing.
     */
    public static final ScopedValue<String> CORRELATION_ID = ScopedValue.newInstance();

    /**
     * Demonstrates: ScopedValue.where().run() - binds a value for a scope.
     * Runs the given task with the authenticated user context set.
     *
     * @param username  the authenticated user
     * @param jwtId     the JWT token ID
     * @param correlationId  request correlation ID
     * @param task      the code to execute within this user context
     */
    public static void runWithUser(String username, String jwtId,
                                   String correlationId, Runnable task) {
        ScopedValue.where(CURRENT_USER, username)
                .where(CURRENT_JWT_ID, jwtId)
                .where(CORRELATION_ID, correlationId)
                .run(task);
    }

    /**
     * Demonstrates: ScopedValue.where().call() - binds a value and returns a result.
     * Runs a computation within the user context and returns the result.
     */
    public static <T> T callWithUser(String username, String jwtId,
                                     String correlationId,
                                     ScopedValue.CallableOp<? extends T, ? extends Exception> task)
            throws Exception {
        return ScopedValue.where(CURRENT_USER, username)
                .where(CURRENT_JWT_ID, jwtId)
                .where(CORRELATION_ID, correlationId)
                .call(task);
    }

    /**
     * Demonstrates: ScopedValue.isBound() and .get() - reading the scoped value.
     * Returns the current user or "anonymous" if not in a scoped context.
     */
    public static String currentUser() {
        return CURRENT_USER.isBound() ? CURRENT_USER.get() : "anonymous";
    }

    public static String currentJwtId() {
        return CURRENT_JWT_ID.isBound() ? CURRENT_JWT_ID.get() : "N/A";
    }

    public static String correlationId() {
        return CORRELATION_ID.isBound() ? CORRELATION_ID.get() : "NO-CORRELATION";
    }
}
