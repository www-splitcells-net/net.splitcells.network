package net.splitcells.website;

public class RenderingConfig {
    public static RenderingConfig renderingConfig() {
        return new RenderingConfig();
    }

    private String generationStyle = "standard";

    private RenderingConfig() {
    }

    public String generationStyle() {
        return generationStyle;
    }

    public RenderingConfig withGenerationStyle(String arg) {
        generationStyle = arg;
        return this;
    }
}
