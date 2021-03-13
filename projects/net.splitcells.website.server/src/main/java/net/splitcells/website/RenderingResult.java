package net.splitcells.website;

public class RenderingResult {
    public static RenderingResult renderingResult(byte[] content, String format) {
        return new RenderingResult(content, format);
    }

    private final byte[] content;
    private final String format;

    private RenderingResult(byte[] content, String format) {
        this.content = content;

        this.format = format;
    }

    public byte[] getContent() {
        return content;
    }

    public String getFormat() {
        return format;
    }
}
