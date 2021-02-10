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

	default <T extends AfterAdditionSubscriber & BeforeRemovalSubscriber> void synchronize(T subscriber) {
		subscribe_to_afterAdditions(subscriber);
		subscriber_to_beforeRemoval(subscriber);
	}

	void subscribe_to_afterAdditions(AfterAdditionSubscriber subscriber);

	void subscriber_to_beforeRemoval(BeforeRemovalSubscriber subscriber);

	void subscriber_to_afterRemoval(BeforeRemovalSubscriber subscriber);
}