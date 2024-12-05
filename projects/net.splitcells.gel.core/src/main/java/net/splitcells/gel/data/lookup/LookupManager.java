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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;

import java.util.function.Predicate;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.data.lookup.LookupI.persistedLookupI;
import static net.splitcells.gel.data.lookup.LookupTables.lookupTable;
import static net.splitcells.gel.data.view.View.INVALID_INDEX;

/**
 * This {@link Lookup} decides, which {@link Lookup} implementation is used, at a given time.
 * This is done, in order to improve the performance.
 *
 * @param <T>
 */
public class LookupManager<T> implements Lookup<T> {
    public static <T> Lookup<T> lookupManager(View view, Attribute<T> attribute) {
        return new LookupManager<>(view, attribute);
    }

    private static final int MIN_STRATEGY_TIME = 100;

    private final View view;
    private final Attribute<T> attribute;
    private final Lookup<T> persistedLookup;
    private boolean isPersistedLookupActive = false;
    private boolean isPersistedLookupForced = false;
    private long lookupReadCount = 0;
    private long lookupWriteCount = 0;
    private long lastStrategyTime = 0;

    private LookupManager(View argView, Attribute<T> argAttribute) {
        view = argView;
        attribute = argAttribute;
        persistedLookup = persistedLookupI(view, attribute, false);
    }

    private void enablePersistedLookup() {
        enablePersistedLookup(INVALID_INDEX, INVALID_INDEX);
    }

    private void enablePersistedLookup(int additionIndex, int removalIndex) {
        isPersistedLookupActive = true;
        lastStrategyTime = 0;
        view.unorderedLinesStream()
                .filter(l -> l.index() != additionIndex)
                .forEach(l -> persistedLookup.register_addition(l.value(attribute), l.index()));
    }

    private void updateStatistics() {
        updateStatistics(INVALID_INDEX, INVALID_INDEX);
    }

    private void updateStatistics(int additionIndex, int removalIndex) {
        if (lastStrategyTime < MIN_STRATEGY_TIME) {
            return;
        }
        if (isPersistedLookupActive) {
            if (lookupReadCount < lookupWriteCount && !isPersistedLookupForced) {
                isPersistedLookupActive = false;
                lastStrategyTime = 0;
                view.unorderedLinesStream()
                        .filter(l -> l.index() != additionIndex && l.index() != removalIndex)
                        .forEach(l -> persistedLookup.register_removal(l.value(attribute), l.index()));
            }
        } else if (lookupReadCount > lookupWriteCount) {
            enablePersistedLookup(additionIndex, removalIndex);
        }
    }

    @Override
    public List<String> path() {
        final List<String> path = view.path();
        path.add(attribute.name());
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
        return persistedLookup.persistedLookup(value);
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
        return persistedLookup.persistedLookup(predicate);
    }

    @Override
    public View lookup(T value) {
        ++lookupReadCount;
        ++lastStrategyTime;
        updateStatistics();
        if (isPersistedLookupActive) {
            return persistedLookup.persistedLookup(value);
        }
        return lookupIntern(a -> a.equals(value));
    }

    @Override
    public View lookup(Predicate<T> predicate) {
        ++lookupReadCount;
        ++lastStrategyTime;
        updateStatistics();
        return lookupIntern(predicate);
    }

    private View lookupIntern(Predicate<T> predicate) {
        if (isPersistedLookupActive) {
            return persistedLookup.persistedLookup(predicate);
        }
        final var lookupBasePath = view.path();
        if (lookupBasePath.hasElements()) {
            lookupBasePath.removeAt(lookupBasePath.size() - 1);
        }
        final var lookup = lookupTable(view, attribute);
        view.unorderedLinesStream()
                .filter(l -> predicate.test(l.value(attribute)))
                .forEach(lookup::register);
        return lookup;
    }

    @Override
    public void register_addition(T addition, int index) {
        ++lookupWriteCount;
        ++lastStrategyTime;
        updateStatistics(index, INVALID_INDEX);
        if (isPersistedLookupActive) {
            persistedLookup.register_addition(addition, index);
        }
    }

    @Override
    public void register_removal(T removal, int index) {
        ++lookupWriteCount;
        ++lastStrategyTime;
        final var wasPersistedLookupActive = isPersistedLookupActive;
        updateStatistics(INVALID_INDEX, index);
        if (wasPersistedLookupActive) {
            persistedLookup.register_removal(removal, index);
        }
    }
}
