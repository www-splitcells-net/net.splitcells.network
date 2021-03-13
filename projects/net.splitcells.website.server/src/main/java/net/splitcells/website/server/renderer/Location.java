package net.splitcells.website.server.renderer;

import java.nio.file.Path;

public class Location {
    public static Location location(Path root, Path relativeChild) {
        return new Location(root, relativeChild);
    }

    private final Path root;
    /**
     * RENAME
     */
    private final Path relativeChild;

    private Location(Path root, Path relativeChild) {
        this.root = root;
        this.relativeChild = relativeChild;
    }

    public Path relativeChild() {
        return relativeChild;
    }

    public Path child() {
        return root.resolve(relativeChild);
    }

    public Path root() {
        return root;
    }
}
