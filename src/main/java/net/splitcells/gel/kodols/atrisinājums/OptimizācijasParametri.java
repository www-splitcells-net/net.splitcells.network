package net.splitcells.gel.kodols.atrisinājums;

import net.splitcells.dem.lang.annotations.Returns_this;

public class OptimizācijasParametri {
    public static OptimizācijasParametri optimizācijasParametri() {
        return new OptimizācijasParametri();
    }
    private boolean dubultuNoņemšanaAtļauts = false;
    private OptimizācijasParametri() {
        
    }
    @Returns_this
    public OptimizācijasParametri arDubultuNoņemšanaAtļauts(boolean arg) {
        dubultuNoņemšanaAtļauts = arg;
        return this;
    }

    public boolean getDubultuNoņemšanaAtļauts() {
        return dubultuNoņemšanaAtļauts;
    }
}
