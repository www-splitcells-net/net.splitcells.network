package net.splitcells.dem.utils;

public class Exception extends RuntimeException {
    public static Exception exception(String message) {
        return new Exception(message);
    }

    private Exception(String message) {
        super(message);
    }
}
