package net.splitcells.dem.resource.communication;

public interface Closeable extends AutoCloseable {

    /**
     * Used in order to circumvent checked exceptions of {@link AutoCloseable#close}.
     */
    void close();
}
