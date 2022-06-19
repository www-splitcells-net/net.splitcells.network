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

import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.utils.FodsUtility.tableCell;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.utils.FodsUtility;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.MetaDataView;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import org.w3c.dom.Element;

/**
 * Provides the availability to reset a given {@link net.splitcells.gel.solution.Solution}
 * to a  previous state.
 * Each state has an index, which identifies a point in the history's timeline.
 * The first state of an {@link net.splitcells.gel.solution.Solution}
 * has the index -1 (where usually no allocations are present).
 * The following states have successive ascending indexes (-1,0,1,2...,n-1).
 * <p>
 * The reason why the first state has the index -1 is caused by the fact,
 * that the history's timeline concept is a list of events.
 * Each event has the changes required to get from the previous state to the
 * next state.
 * Therefore, the first event has the index 0.
 * The first state recorded by the history does not require any changes,
 * because there is no previous state from the perspective of the timeline.
 * In other words, the first state is implicitly recorded by the history,
 * whereas the following states are explicitly recorded.
 * <p>
 * IDEA History should only contain primary demand/supply references and no references to used or unused demand/supply,
 * in order to preserve line pointer validity.
 */
public interface History extends Allocations, AfterAdditionSubscriber, BeforeRemovalSubscriber {
    Attribute<Integer> ALLOCATION_ID = attribute(Integer.class, "allocation-id");
    Attribute<Allocation> ALLOCATION_EVENT = attribute(Allocation.class, "allocation-event");
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
     * <p>During {@link Runnable#run()} this object does not log the history of the tracked {@link net.splitcells.gel.solution.Solution}.</p>
     * <p>TODO IDEA Create an event marking, that this method was called.</p>
     *
     * @param runnable This is the code to be run during which the {@link History} is disabled.
     */
    void processWithoutHistory(Runnable runnable);

    /**
     * @return Number Of Events Since History Start
     */
    int currentIndex();

    /**
     * <p>TODO Use a better name for this method.</p>
     * <p>TODO FIX Does not work on empty histories.</p>
     *
     * @return Returns a FODS file of the table similar to {@link #toFods()},
     * but where each field of the complex {@link Attribute}s gets a distinct colum.
     * This makes it easier to analyse the history, by improving the searchability by column values.
     */
    default Element toAnalysisFods() {
        final var fods = rElement(FODS_OFFICE, "document");
        final var body = elementWithChildren(FODS_OFFICE, "body");
        fods.setAttributeNode
                (Xml.attribute(FODS_OFFICE, "mimetype", "application/vnd.oasis.opendocument.spreadsheet"));
        fods.appendChild(body);
        {
            final var spreadsheet = elementWithChildren(FODS_OFFICE, "spreadsheet");
            body.appendChild(spreadsheet);
            final var table = rElement(FODS_TABLE, "table");
            spreadsheet.appendChild(table);
            table.setAttributeNode(Xml.attribute(FODS_TABLE, "name", "values"));
            {
                final var header = elementWithChildren(FODS_TABLE, "table-row");
                table.appendChild(header);
                header.appendChild(tableCell(ALLOCATION_ID.name()));
                header.appendChild(tableCell("allocation-type"));
                if (!getLines().isEmpty()) {
                    // TODO HACK Prevents errors if the history is empty.
                    getLines().get(0).value(ALLOCATION_EVENT).demand().context().headerView().forEach(a -> header.appendChild(tableCell("demand-" + a.name())));
                    getLines().get(0).value(ALLOCATION_EVENT).supply().context().headerView().forEach(a -> header.appendChild(tableCell("supply-" + a.name())));
                }
                header.appendChild(tableCell("allocation-cost"));
                header.appendChild(tableCell("complete-cost"));
                header.appendChild(tableCell(META_DATA.name()));
                getLines().forEach(line -> {
                    final var tableLine = elementWithChildren(FODS_TABLE, "table-row");
                    table.appendChild(tableLine);
                    tableLine.appendChild(tableCell("" + line.value(ALLOCATION_ID)));
                    tableLine.appendChild(tableCell(line.value(ALLOCATION_EVENT).type().name()));
                    line.value(ALLOCATION_EVENT).demand().context().headerView().forEach(a -> tableLine.appendChild(tableCell(line.value(ALLOCATION_EVENT).demand().value(a).toString())));
                    line.value(ALLOCATION_EVENT).supply().context().headerView().forEach(a -> tableLine.appendChild(tableCell(line.value(ALLOCATION_EVENT).supply().value(a).toString())));
                    tableLine.appendChild(tableCell("" + line.value(META_DATA).value(AllocationRating.class).get().value().asMetaRating().getContentValue(Cost.class).value()));
                    tableLine.appendChild(tableCell("" + line.value(META_DATA).value(CompleteRating.class).get().value().asMetaRating().getContentValue(Cost.class).value()));
                    tableLine.appendChild(tableCell(toPrettyString(line.value(META_DATA).toDom())));
                });
            }
        }
        return fods;
    }
}
