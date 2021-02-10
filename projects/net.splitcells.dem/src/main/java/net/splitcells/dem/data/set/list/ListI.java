package net.splitcells.dem.data.set.list;

import java.util.ArrayList;
import java.util.Collection;

public class ListI<T> extends ArrayList<T> implements List<T> {

	/**
     * TODO Make private.
	 */
	protected ListI() {
	}

	public ListI(Collection<T> elements) {
		super(elements);
	}
}
