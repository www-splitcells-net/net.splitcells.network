package net.splitcells.gel.solution.history;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.Assertions.assertThat;

public class HistoryRef extends HistoryI {
    protected HistoryRef(Solution solution) {
        super(solution);
    }

    /**
     * "-1" stands for the initial state of the solution.
     * The target index should not be reverted itself.
     *
     * @param index
     */
    @Override
    public void resetTo(int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(this.size()).isGreaterThan(index);
            assertThat(index).isGreaterThanOrEqualTo(-1);
        }
        super.resetTo(index);
    }

    @Override
    protected void resetToInOrder(List<Integer> indekses) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(indekses).isNotEmpty();
        }
        super.resetToInOrder(indekses);
    }

    @Override
    protected void resetLast() {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(columnView(ALLOCATION_ID).lookup(size() - 1).size()).isEqualTo(1);
        }
        super.resetLast();
    }

    @Override
    protected void resetLast_removal(int indexes) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(size() - 1).isEqualTo(indexes + 1);
            try {
                assertThat(columnView(ALLOCATION_ID).lookup(indexes + 1).size()).isEqualTo(1);
                assertThat(columnView(ALLOCATION_ID).lookup(indexes).size()).isEqualTo(1);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        super.resetLast_removal(indexes);
    }

    /**
     * TODO FIX Remove unused demands and supply.
     * <p/>
     * TODO Automatically remove allocations from solution, via subscription.
     *
     * @param line
     */
    @Override
    protected void removal_(Line line) {
        super.removal_(line);
    }
}
