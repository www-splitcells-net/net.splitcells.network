package net.splitcells.gel.solution.history;

import net.splitcells.gel.solution.Solution;

public class HistoryRefFactory implements HistoryFactory {
    @Override
    public History history(Solution solution) {
        return new HistoryRef(solution);
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
