package net.splitcells.gel.solution;

import net.splitcells.dem.lang.annotations.Returns_this;

public class OptimizationParameters {
    public static OptimizationParameters optimizationParameters() {
        return new OptimizationParameters();
    }
    private boolean dublicateRemovalAllowed = false;
    private OptimizationParameters() {
        
    }
    @Returns_this
    public OptimizationParameters arDubultuNoņemšanaAtļauts(boolean arg) {
        dublicateRemovalAllowed = arg;
        return this;
    }

    public boolean dublicateRemovalAllowed() {
        return dublicateRemovalAllowed;
    }
}
