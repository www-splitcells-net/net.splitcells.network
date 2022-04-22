package net.splitcells.website.server.project;

import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.Optional;

/**
 * This config contains the metadata of a path/document,
 * which is used in order to improve its rendering.
 */
public class LayoutConfig {
    public static LayoutConfig layoutConfig(String path) {
        return new LayoutConfig(path);
    }

    private final String path;
    private Optional<String> title = Optional.empty();

    private LayoutConfig(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public Optional<String> title() {
        return title;
    }

    public LayoutConfig withTitle(String title) {
        this.title = Optional.of(title);
        return this;
    }
}
