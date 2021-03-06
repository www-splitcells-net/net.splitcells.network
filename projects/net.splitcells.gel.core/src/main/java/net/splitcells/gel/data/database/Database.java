package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

public interface Database extends Table {

    Line addTranslated(List<? extends Object> values);

    Line add(Line line);

    @Deprecated
    void remove(int lineIndex);

    void remove(Line line);

    default void replace(Line newLine) {
        if (null != rawLinesView().get(newLine.index())) {
            remove(newLine.index());
        }
        add(newLine);
    }

    /**
     * This methods signifies, that the removal and addition subscribers belong together.
     *
     * @param subscriber This is the synchronize.
     */
    default void synchronize(DatabaseSynchronization subscriber) {
        subscribeToAfterAdditions(subscriber);
        subscriberToBeforeRemoval(subscriber);
    }

    void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber);

    void subscriberToBeforeRemoval(BeforeRemovalSubscriber subscriber);

    void subscriberToAfterRemoval(BeforeRemovalSubscriber subscriber);
}