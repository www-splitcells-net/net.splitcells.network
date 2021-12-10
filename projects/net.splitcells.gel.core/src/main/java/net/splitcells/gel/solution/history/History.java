/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.MetaDataView;

/**
 * Provides the availability to reset a given {@link net.splitcells.gel.solution.Solution}
 * to a  previous state.
 * Each state has an index, which identifies a point in the history's timeline.
 * The first state of an {@link net.splitcells.gel.solution.Solution}
 * has the index -1 (where usually no allocations are present).
 * The following states have successive ascending indexes (-1,0,1,2...n).
 *
 * The reason why the first state has the index -1 is caused by the fact,
 * that the history's timeline concept is a list of events.
 * Each event has the changes required to get from the previous state to the
 * next state.
 * Therefore, the first event has the index 0.
 * The first state recorded by the history does not require any changes,
 * because there is no previous state from the perspective of the timeline.
 * In other words, the first state is implicitly recorded by the history,
 * whereas the following states are explicitly recorded.
 *
 * IDEA History should only contain primary demand/supply references and no references to used or unused demand/supply,
 * in order to preserve line pointer validity.
 */
public interface History extends Allocations, AfterAdditionSubscriber, BeforeRemovalSubscriber {
    Attribute<Integer> ALLOCATION_ID = attribute(Integer.class, "allocation-id");
    Attribute<Allocation> ALLOCATION_EVENT = attribute(Allocation.class, "allocation-notikums");
    Attribute<MetaDataView> META_DATA = attribute(MetaDataView.class, "meta-data");

    /**
     * Undoes actions recorded by this {@link History} up to the point in
     * timeline,
     * where the {@link #currentIndex()} was {@param index} the last time.
     * After executing this method, jumping back to a future state is not
     * possible in general.
     * 
     * @param index {@link #currentIndex()} After The Reset
     */
    void resetTo(int index);

    /**
     * @return Number Of Events Since History Start
     */
    int currentIndex();
}
