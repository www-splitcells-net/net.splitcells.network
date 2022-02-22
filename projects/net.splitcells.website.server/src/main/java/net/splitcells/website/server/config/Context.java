package net.splitcells.website.server.config;

import java.util.Optional;

public class Context {
    public static Context context() {
        return new Context();
    }

    private Optional<String> layout = Optional.empty();

    private Context() {

    }

    public Context withLayout(String layout) {
        this.layout = Optional.of(layout);
        return this;
    }

    public Optional<String> layout() {
        return layout;
    }
}
