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
    boolean is_true();

    /**
     * RENAME
     */
    boolean is_false();

    @SuppressWarnings("unchecked")
    @Returns_this
    public default <R extends DeepCloneable> R required() {
        if (is_false()) {
            throw new RuntimeException();
        }
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    @Returns_this
    public default <R extends DeepCloneable> R if_(Runnable run) {
        if (is_true()) {
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
        if (is_false()) {
            run.run();
        }
        return (R) this;
    }

}
