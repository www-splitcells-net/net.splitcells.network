package net.splitcells.dem;

import net.splitcells.dem.data.atom.Bool;

import static net.splitcells.dem.data.atom.Bools.bool;

public class ProcessResult {
    public static ProcessResult processResult() {
        return new ProcessResult();
    }

    private boolean hasError = false;

    private ProcessResult() {
    }

    public void hasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean hasError() {
        return hasError;
    }

    public void requireErrorFree() {
        if (hasError) {
            throw new RuntimeException();
        }
    }
}
