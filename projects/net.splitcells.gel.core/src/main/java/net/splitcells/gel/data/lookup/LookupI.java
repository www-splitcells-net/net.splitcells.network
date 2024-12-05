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

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.gel.data.lookup.LookupTables.lookupTable;

import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.attribute.Attribute;

/**
 * <p>Provides a view to a subset of a {@link View} as a {@link View}.
 * {@link Line} that are part via the subset are either determined and accessed by a {@link Attribute} value or via custom a {@link Predicate}.
 * </p>
 *
 * @param <T>
 */
public class LookupI<T> implements Lookup<T> {

    /**
     * TODO This needs to be tested and probably only enabled for only some {@link LookupI}.
     */
    private static final boolean EXPERIMENTAL_SPEED_UP = false;

    public static <T> Lookup<T> persistedLookupI(View view, Attribute<T> attribute) {
        return new LookupI<>(view, attribute, true);
    }

    public static <T> Lookup<T> persistedLookupI(View view, Attribute<T> attribute, boolean isPersisted) {
        return new LookupI<>(view, attribute, isPersisted);
    }

    private final PersistedLookupView lookupTable;

    private final View view;
    private final Map<T, PersistedLookupView> content = map();
    private final Attribute<T> attribute;
    private final Map<Predicate<T>, PersistedLookupView> complexContent = map();

    private LookupI(View view, Attribute<T> attribute, boolean isPersisted) {
        this.view = view;
        this.lookupTable = lookupTable(view, attribute);
        this.attribute = attribute;
        if (isPersisted) {
            view.unorderedLinesStream().forEach(e -> register_addition(e.value(attribute), e.index()));
        }
    }

    @Override
    public void register_addition(T addition, int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            require(view.rawLinesView().size() > index);
        }
        {
            final PersistedLookupView lookupTable;
            if (content.containsKey(addition)) {
                lookupTable = content.get(addition);
            } else {
                lookupTable = lookupTable(view, attribute, false);
                content.put(addition, lookupTable);
            }
            if (lookupTable.isRawLine(index)) {
                // The given line was already added.
                return;
            }
            lookupTable.register(view.rawLine(index));
        }
        register_beforeAddition_atComplexContent(addition, index);
    }

    private void register_beforeAddition_atComplexContent(T addition, int index) {
        complexContent.forEach((predicate, lookupTable) -> {
            if (predicate.test(addition)) {
                lookupTable.register(view.rawLine(index));
            }
        });
    }

    @Override
    public void register_removal(T removal, int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            requireNotNull(content.get(removal).rawLinesView().get(index));
        }
        final var line = view.rawLine(index);
        final var affectedLookupTable = content.get(removal);
        affectedLookupTable.removeRegistration(line);
        final var complexLookupTablesToRemove = Lists.<Predicate<T>>list();
        if (EXPERIMENTAL_SPEED_UP) {
            complexContent.forEach((predicate, lookupTable) -> {
                if (predicate.test(removal)) {
                    lookupTable.removeRegistration(view.rawLinesView().get(index));
                    if (lookupTable.isEmpty()) {
                        complexLookupTablesToRemove.add(predicate);
                    }
                }
            });
            // garbage collection
            complexLookupTablesToRemove.forEach(complexContent::remove);
        } else {
            complexContent.forEach((predicate, lookupTable) -> {
                if (predicate.test(removal)) {
                    lookupTable.removeRegistration(view.rawLinesView().get(index));
                }
            });
        }
        // garbage collection
        if (affectedLookupTable.isEmpty()) {
            content.remove(removal);
        }
    }

    @Override
    public View persistedLookup(T value) {
        if (content.containsKey(value)) {
            return content.get(value);
        }
        return lookupTable;
    }

    @Override
    public View persistedLookup(Predicate<T> predicate) {
        if (!complexContent.containsKey(predicate)) {
            final var lookup = LookupTables.lookupTable(view, predicate.toString());
            complexContent.put(predicate, lookup);
            view
                    .rawLinesView()
                    .stream()
                    .filter(e -> e != null)
                    .forEach(e -> {
                        if (predicate.test(e.value(attribute))) {
                            lookup.register(e);
                        }
                    });
        }
        return complexContent.get(predicate);
    }

    @Override
    public List<String> path() {
        final List<String> path = view.path();
        path.add(attribute.name());
        return path;
    }
}
