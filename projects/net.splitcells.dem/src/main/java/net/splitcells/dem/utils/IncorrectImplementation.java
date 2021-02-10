package net.splitcells.dem.utils;

public class IncorrectImplementation extends RuntimeException {
    public static IncorrectImplementation incorrectImplementation(String message) {
        return new IncorrectImplementation(message);
    }

    private IncorrectImplementation(String message) {
        super(message);
    }
}
