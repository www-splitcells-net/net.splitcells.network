package net.splitcells.dem.lang.namespace;

public class NameSpace {

    public static NameSpace nameSpace(String prefix, String uri) {
        return new NameSpace(prefix, uri);
    }

    private final String uri;
    private final String prefix;

    private NameSpace(String prefix, String uri) {
        this.uri = uri;
        this.prefix = prefix;

    }

    public String uri() {
        return uri;
    }

    public String defaultPrefix() {
        return prefix;
    }

    public String prefixedName(String name) {
        return prefix + ":" + name;
    }
}
