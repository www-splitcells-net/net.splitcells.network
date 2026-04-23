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
package net.splitcells.gel.data.lookup;

import lombok.val;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;

import java.util.function.Predicate;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.lookup.LookupColumnImpl.lookupColumnImpl;
import static net.splitcells.gel.data.lookup.LookupTables.lookupTable;
import static net.splitcells.gel.data.view.View.INVALID_INDEX;

/**
 * This {@link LookupColumn} decides, which {@link LookupColumn} implementation is used, at a given time.
 * This is done, in order to improve the performance.
 *
 * @param <T> The type of value, that is looked up.
 */
public class LookupManager<T> implements LookupColumn<T> {
    public static final int DEFAULT_MIN_STRATEGY_TIME = 100;

    public static <T> LookupManager<T> lookupManager(View view, Attribute<T> attribute) {
        return new LookupManager<>(view, attribute, DEFAULT_MIN_STRATEGY_TIME);
    }

    public static <T> LookupManager<T> lookupManager(View view, Attribute<T> attribute, int minStrategyTime) {
        return new LookupManager<>(view, attribute, minStrategyTime);
    }

    private final View view;
    private final Attribute<T> attribute;
    private final LookupColumn<T> lookupColumn;
    private boolean isPersistedLookupActive = false;
    private boolean isPersistedLookupForced = false;
    private long lookupReadCount = 0;
    private long lookupWriteCount = 0;
    private long lastStrategyTime = 0;
    private final int minStrategyTime;
    private final ListView<String> path;

    private LookupManager(View argView, Attribute<T> argAttribute, int argMinStrategyTime) {
        view = argView;
        attribute = argAttribute;
        lookupColumn = lookupColumnImpl(view, attribute, false);
        minStrategyTime = argMinStrategyTime;
        path = view.path().shallowCopy().withAppended(attribute.name());
    }

    private void enablePersistedLookup() {
        enablePersistedLookup(INVALID_INDEX, INVALID_INDEX);
    }

    private void enablePersistedLookup(int additionIndex, int removalIndex) {
        isPersistedLookupActive = true;
        lastStrategyTime = 0;
        view.unorderedLinesStream()
                .filter(l -> l.index() != additionIndex && l.index() != removalIndex)
                .forEach(l -> lookupColumn.register_addition(l.value(attribute), l.index()));
    }

    private void updateStatistics() {
        updateStatistics(INVALID_INDEX, INVALID_INDEX);
    }

    private void updateStatistics(int additionIndex, int removalIndex) {
        if (lastStrategyTime < minStrategyTime) {
            return;
        }
        if (isPersistedLookupActive) {
            if (lookupReadCount < lookupWriteCount && !isPersistedLookupForced) {
                isPersistedLookupActive = false;
                lastStrategyTime = 0;
                view.unorderedLinesStream()
                        .filter(l -> l.index() != additionIndex && l.index() != removalIndex)
                        .forEach(l -> lookupColumn.register_removal(l.value(attribute), l.index()));
            }
        } else if (lookupReadCount > lookupWriteCount) {
            enablePersistedLookup(additionIndex, removalIndex);
        }
    }

    @Override
    public ListView<String> path() {
        return path;
    }

    @Override
    public View persistedLookup(T value) {
        ++lookupReadCount;
        ++lastStrategyTime;
        updateStatistics();
        if (!isPersistedLookupActive) {
            isPersistedLookupForced = true;
            enablePersistedLookup();
        }
        return lookupColumn.persistedLookup(value);
    }

    @Override
    public View persistedLookup(Predicate<T> predicate) {
        ++lookupReadCount;
        ++lastStrategyTime;
        updateStatistics();
        if (!isPersistedLookupActive) {
            isPersistedLookupForced = true;
            enablePersistedLookup();
        }
        return lookupColumn.persistedLookup(predicate);
    }

    @Override
    public View lookup(T value) {
        ++lookupReadCount;
        ++lastStrategyTime;
        updateStatistics();
        if (isPersistedLookupActive) {
            return lookupColumn.persistedLookup(value);
        }
        return lookupIntern(a -> a.equals(value));
    }

    @Override
    public View lookup(Predicate<T> predicate) {
        ++lastStrategyTime;
        updateStatistics();
        return lookupIntern(predicate);
    }

    private View lookupIntern(Predicate<T> predicate) {
        if (isPersistedLookupActive) {
            val lookup = lookupColumn.persistedLookup(predicate);
            lookupReadCount += lookup.size();
            return lookup;
        }
        final var lookup = lookupTable(view, attribute);
        view.unorderedLinesStream2()
                .filter(l -> predicate.test(l.value(attribute)))
                .forEach(l -> {
                    ++lookupReadCount;
                    lookup.register(l);
                });
        return lookup;
    }

    @Override
    public void register_addition(T addition, int index) {
        ++lookupWriteCount;
        ++lastStrategyTime;
        updateStatistics(index, INVALID_INDEX);
        if (isPersistedLookupActive) {
            lookupColumn.register_addition(addition, index);
        }
    }

    @Override
    public void register_removal(T removal, int index) {
        ++lookupWriteCount;
        ++lastStrategyTime;
        final var wasPersistedLookupActive = isPersistedLookupActive;
        updateStatistics(INVALID_INDEX, index);
        if (wasPersistedLookupActive) {
            lookupColumn.register_removal(removal, index);
        }
    }

    public boolean isPersistedLookupActive() {
        return isPersistedLookupActive;
    }
}
