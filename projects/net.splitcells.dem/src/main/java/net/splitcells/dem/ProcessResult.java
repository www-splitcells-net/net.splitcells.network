package net.splitcells.dem;

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
}
