package net.splitcells.dem.utils;

public final class Not_implemented_yet extends RuntimeException {

    public static Not_implemented_yet not_implemented_yet() {
        return new Not_implemented_yet();
    }

    /**
     * This generally used in default implementation in order to see the implementing class causing this error.
     */
    public static Not_implemented_yet not_implemented_yet(String message) {
        return new Not_implemented_yet(message);
    }

    @Deprecated
    public Not_implemented_yet() {

    }

    private Not_implemented_yet(String message) {
        super(message);
    }

}
