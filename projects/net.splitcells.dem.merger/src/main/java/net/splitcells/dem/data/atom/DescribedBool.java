package net.splitcells.dem.data.atom;

import java.util.function.Supplier;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class DescribedBool implements Bool {

    public static Bool describedBool(boolean content, Supplier<String> description) {
        return new DescribedBool(content, description);
    }

    private final boolean content;
    private final Supplier<String> description;

    private DescribedBool(boolean content, Supplier<String> description) {
        this.content = content;
        this.description = description;
    }

    @Override
    public boolean toJavaPrimitive() {
        return content;
    }

    @Override
    public Bool set(boolean arg) {
        throw notImplementedYet();
    }

    @Override
    public Bool or(Bool arg) {
        throw notImplementedYet();
    }

    @Override
    public Bool xor(Bool arg) {
        throw notImplementedYet();
    }

    @Override
    public Bool not() {
        throw notImplementedYet();
    }

    @Override
    public Bool and(Bool arg) {
        throw notImplementedYet();
    }

    @Override
    public boolean isTrue() {
        return content;
    }

    @Override
    public boolean isFalse() {
        return !content;
    }

    @Override
    public Bool required() {
        if (!content) {
            throw new RuntimeException("The following is required, but not present: " + description.get());
        }
        return this;
    }

    @Override
    public Bool requireFalse() {
        if (content) {
            throw new RuntimeException("The following is prohibited, but is present: " + description.get());
        }
        return this;
    }
}
