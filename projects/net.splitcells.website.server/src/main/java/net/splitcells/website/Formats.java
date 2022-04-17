package net.splitcells.website;

public enum Formats {
    HTML("text/html")
    , CSS("text/css")
    , TEXT_PLAIN("text/plain");

    private final String mimeTypes;

    Formats(String mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public String mimeTypes() {
        return mimeTypes;
    }
}
