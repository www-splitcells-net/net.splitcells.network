package net.splitcells.gel.solution.history;

import net.splitcells.gel.solution.Solution;

public class HistoryIFactory implements HistoryFactory {

    @Override
    public History vēsture(Solution solution) {
        return new HistoryI(solution);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
