package com.chemistry.demo.utils;

public final class SeederExecutionContext {

    private static final ThreadLocal<Boolean> SEEDING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private SeederExecutionContext() {
    }

    public static void enter() {
        SEEDING.set(Boolean.TRUE);
    }

    public static void exit() {
        SEEDING.remove();
    }

    public static boolean isActive() {
        return Boolean.TRUE.equals(SEEDING.get());
    }
}
