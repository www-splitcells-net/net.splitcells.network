package net.splitcells.dem.data.atom;

import net.splitcells.dem.lang.annotations.Returns_this;
import net.splitcells.dem.object.DeepCloneable;
import net.splitcells.dem.object.ShallowCopyable;

public interface Bool extends ShallowCopyable, DeepCloneable {

    boolean toJavaPrimitive();

    Bool set(boolean arg);

    Bool or(Bool arg);

    Bool xor(Bool arg);

    Bool not();

    Bool and(Bool arg);

    Bool nand(Bool arg);

    Bool nor(Bool arg);

    Bool xnor(Bool arg);

    /**
     * RENAME
     */
    boolean isTrue();

    /**
     * RENAME
     */
    boolean isFalse();

    @SuppressWarnings("unchecked")
    @Returns_this
    public default <R extends DeepCloneable> R required() {
        if (isFalse()) {
            throw new RuntimeException();
        }
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    @Returns_this
    public default <R extends DeepCloneable> R if_(Runnable run) {
        if (isTrue()) {
            run.run();
        }
        return (R) this;
    }

    /**
     * RENAME ?
     */
    @SuppressWarnings("unchecked")
    @Returns_this
    public default <R extends DeepCloneable> R if_not(Runnable run) {
        if (isFalse()) {
            run.run();
        }
        return (R) this;
    }

}
