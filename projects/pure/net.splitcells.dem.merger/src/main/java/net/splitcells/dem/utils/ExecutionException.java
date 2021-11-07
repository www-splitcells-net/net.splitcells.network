package net.splitcells.dem.utils;

public class ExecutionException extends RuntimeException {
    public static ExecutionException executionException(String message) {
        return new ExecutionException(message);
    }

    private ExecutionException(String message) {
        super(message);
    }
}
