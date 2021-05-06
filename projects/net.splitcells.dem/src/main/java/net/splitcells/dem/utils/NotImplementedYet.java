package net.splitcells.dem.utils;

public final class NotImplementedYet extends RuntimeException {

    public static NotImplementedYet notImplementedYet() {
        return new NotImplementedYet();
    }
    
    public static NotImplementedYet not_implemented_yet(String message) {
        return new NotImplementedYet(message);
    }

    @Deprecated
    public NotImplementedYet() {

    }

    private NotImplementedYet(String message) {
        super(message);
    }

}
