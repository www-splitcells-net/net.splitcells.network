package net.splitcells.dem.resource;

public enum ContentType {
    CSV("csv");
    private final String codeName;

    ContentType(String codeName) {
        this.codeName = codeName;
    }

    public String codeName() {
        return codeName;
    }
}
