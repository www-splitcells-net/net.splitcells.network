/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history;

import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.lang.perspective.XmlConfig.xmlConfig;
import static net.splitcells.dem.utils.FodsUtility.tableCell;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

import net.splitcells.dem.lang.perspective.Tree;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.type.Cost;
import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.MetaDataView;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import net.splitcells.gel.solution.Solution;

import java.util.function.Supplier;

/**
 * <p>Provides the availability to reset a given {@link Solution}
 * to a  previous state.
 * Each state has an index, which identifies a point in the history's timeline.
 * The first state of an {@link Solution}
 * has the index -1 (where usually no allocations are present).
 * The following states have successive ascending indexes (-1,0,1,2...,n-1).</p>
 * <p>
 * The reason why the first state has the index -1 is caused by the fact,
 * that the history's timeline concept is a list of events.
 * Each event has the changes required to get from the previous state to the
 * next state.
 * Therefore, the first event has the index 0.
 * The first state recorded by the history does not require any changes,
 * because there is no previous state from the perspective of the timeline.
 * In other words, the first state is implicitly recorded by the history,
 * whereas the following states are explicitly recorded.</p>
 * <p>TODO There does not seem to be a need, for this to be an {@link Assignments} instead of {@link Table}.
 * Unfortunately, this fact degrades the performance.</p>
 * <p>IDEA History should only contain primary demand/supply references and no references to used or unused demand/supply,
 * in order to preserve line pointer validity.</p>
 */
public interface History extends Assignments, AfterAdditionSubscriber, BeforeRemovalSubscriber {
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
     * <p>This method indicates,
     * that during the {@link Runnable#run()} this object may not log the history of the tracked {@link Solution}.
     * If the history is disabled, the runtime performance is thereby improved.
     * Note, that there is no guarantee, that the {@link Solution#history()} is in fact disabled,
     * because the caller might require the {@link Solution#history()}.
     * In other words, this object's state and implementation decides, if the {@link Solution} history will be recorded or not.
     * </p>
     * <p>TODO IDEA Create an event marking, that this method was called.</p>
     *
     * @param runnable This is the code to be run during which the {@link History} is allowed to be disabled.
     */
    void processWithoutHistory(Runnable runnable);

    /**
     * <p>This method guarantees,
     * that during the {@link Runnable#run()} this object logs the history of the tracked {@link Solution}.
     * If the history is enabled, the runtime performance is thereby deteriorated.
     * </p>
     *
     * @param runnable This is the code to be run during which the {@link History} is enabled.
     */
    void processWithHistory(Runnable runnable);

    /**
     * <p>This method guarantees,
     * that during the {@link Supplier#get()} this object logs the history of the tracked {@link Solution}.
     * If the history is enabled, the runtime performance is thereby deteriorated.
     * </p>
     *
     * @param supplier This is the code to be run during which the {@link History} is enabled.
     */
    <T> T supplyWithHistory(Supplier<T> supplier);

    /**
     * Marks if the {@link History} is consistent, with the tracked {@link Solution}.
     * If this is false, {@link #resetTo(int)} cannot be used.
     * An history is not consistent, if the tracked {@link Solution},
     * was changed, while {@link #isRegisterEventIsEnabled()} was false.
     *
     * @return Consistency Of The {@link History}
     */
    boolean isHistoryConsistent();

    /**
     * Signals if the {@link History} records changes to the tracked {@link Solution}.
     * If this is the case, {@link #isHistoryConsistent()} is going to be false.
     *
     * @return Tracking State Of The {@link Solution}
     */
    boolean isRegisterEventIsEnabled();

    /**
     * Signals, if this should record changes to the tracked {@link Solution}.
     * This is an alternative to {@link #processWithHistory} and similar,
     * but without the use of lambdas.
     *
     * @param arg This is the new value for the option.
     * @return this
     */
    History withRegisterEventIsEnabled(boolean arg);

    History withLogNaturalArgumentation(boolean logNaturalArgumentation);

    /**
     * @return Determines whether {@link #unorderedLines()} contain {@link MetaDataView} with {@link net.splitcells.gel.solution.history.meta.type.AllocationNaturalArgumentation}.
     * If set to false, this will improve performance, but the {@link History} will contain less info.
     * The default value is false.
     */
    boolean logNaturalArgumentation();

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
    default Tree toAnalysisFods() {
        final var fods = perspective("document", FODS_OFFICE)
                .withXmlAttribute("mimetype", "application/vnd.oasis.opendocument.spreadsheet", FODS_OFFICE);
        final var body = perspective("body", FODS_OFFICE);
        fods.withChild(body);
        final var spreadSheet = perspective("spreadsheet", FODS_OFFICE);
        body.withChild(spreadSheet);
        final var table = perspective("table", FODS_TABLE);
        spreadSheet.withChild(table);
        table.withProperty("name", FODS_TABLE, "values");
        final var header = perspective("table-row", FODS_TABLE);
        table.withChild(header);
        headerView().stream()
                .map(att -> att.name())
                .map(attName -> {
                    final var cell = perspective("table-cell", FODS_TABLE);
                    cell.withChild(perspective("p", FODS_TEXT).withChild(perspective(attName)));
                    return cell;
                }).forEach(attDesc -> header.withChild(attDesc));
        if (!unorderedLines().isEmpty()) {
            // TODO HACK Prevents errors if the history is empty.
            unorderedLines().get(0).value(ALLOCATION_EVENT).demand().context().headerView()
                    .forEach(a -> header.withChild(tableCell("demand-" + a.name())));
            unorderedLines().get(0).value(ALLOCATION_EVENT).supply().context().headerView()
                    .forEach(a -> header.withChild(tableCell("supply-" + a.name())));
        }
        header.withChild(tableCell("allocation-cost"));
        header.withChild(tableCell("complete-cost"));
        header.withChild(tableCell(META_DATA.name()));
        unorderedLines().forEach(line -> {
            final var tableLine = perspective("table-row", FODS_TABLE);
            table.withChild(tableLine);
            tableLine.withChild(tableCell("" + line.value(ALLOCATION_ID)));
            tableLine.withChild(tableCell(line.value(ALLOCATION_EVENT).type().name()));
            line.value(ALLOCATION_EVENT).demand().context().headerView()
                    .forEach(a -> tableLine.withChild(tableCell(line.value(ALLOCATION_EVENT).demand().value(a).toString())));
            line.value(ALLOCATION_EVENT).supply().context().headerView()
                    .forEach(a -> tableLine.withChild(tableCell(line.value(ALLOCATION_EVENT).supply().value(a).toString())));
            tableLine.withChild(tableCell("1"));
            tableLine.withChild(tableCell("2"));
            tableLine.withChild(tableCell("" + line.value(META_DATA).value(AllocationRating.class).get().value().asMetaRating().getContentValue(Cost.class).value()));
            tableLine.withChild(tableCell("" + line.value(META_DATA).value(CompleteRating.class).get().value().asMetaRating().getContentValue(Cost.class).value()));
            tableLine.withChild(tableCell(line.value(META_DATA).toPerspective().toXmlString(xmlConfig().withPrintXmlDeclaration(false))));
        });
        return fods;
    }
}
