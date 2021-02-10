package net.splitcells.dem.data.atom;

import net.splitcells.dem.lang.annotations.JavaLegacy;

public class BoolI implements Bool {
    private final boolean value;

    public static Bool bool(boolean arg) {
        return new BoolI(arg);
    }

    public static Bool truthful() {
        return new BoolI(true);
    }

    public static Bool untrue() {
        return new BoolI(false);
    }

    @Deprecated
    public BoolI(final boolean arg_val) {
        value = arg_val;
    }

    @Override
    public boolean is_true() {
        return value;
    }

    @Override
    public boolean is_false() {
        return !value;
    }

    @Override
    public Bool or(Bool arg) {
        return new BoolI(value || arg.is_true());
    }

    @Override
    public Bool set(boolean arg) {
        return new BoolI(arg);
    }

    @Override
    public Bool xor(Bool arg) {
        return new BoolI(value != arg.is_true());
    }

    @Override
    public Bool not() {
        return new BoolI(!value);
    }

    @Override
    public Bool and(Bool arg) {
        return new BoolI(value && arg.is_true());
    }

    @Override
    public Bool nand(Bool arg) {
        return this.and(arg).not();
    }

    @Override
    public Bool nor(Bool arg) {
        return or(arg).not();
    }

    @Override
    public Bool xnor(Bool arg) {
        return xor(arg).not();
    }

    public Bool equals(Bool arg) {
        if (value == arg.is_true()) {
            return new BoolI(true);
        } else {
            return new BoolI(false);
        }
    }

    @Override
    public boolean equals(Object arg) {
        return equals((Bool) arg).is_true();
    }

    @Override
    public BoolI clone() {
        return deepClone(BoolI.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R deepClone(Class<? extends R> arg) {
        return (R) new BoolI(this.value);
    }

    @JavaLegacy
    @Override
    public boolean toJavaPrimitive() {
        return value;
    }

    @Deprecated
    public <A extends Bool> Bool equalContent(A arg) {
        return bool(value == arg.toJavaPrimitive());
    }

}
