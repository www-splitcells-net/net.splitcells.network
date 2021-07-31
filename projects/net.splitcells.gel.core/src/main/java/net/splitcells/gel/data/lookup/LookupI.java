/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.data.lookup;

import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.data.lookup.LookupTable.lookupTable;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

public class LookupI<T> implements Lookup<T> {
    private final LookupTable lookupTable;

    protected final Table table;
    protected final Map<T, LookupTable> content = map();
    protected final Attribute<T> attribute;
    protected final Map<Predicate<T>, LookupTable> complexContent = map();

    protected LookupI(Table table, Attribute<T> attribute) {
        this.table = table;
        this.lookupTable = lookupTable(table, attribute);
        this.attribute = attribute;
        table.rawLinesView().stream()
                .filter(e -> e != null)
                .forEach(e -> register_addition(e.value(attribute), e.index()));
    }

    @Override
    public void register_addition(T addition, int index) {
        {
            final LookupTable lookupTable;
            if (content.containsKey(addition)) {
                lookupTable = content.get(addition);
            } else {
                lookupTable = lookupTable(table, attribute);
                content.put(addition, lookupTable);
            }
            lookupTable.register(table.rawLinesView().get(index));
        }
        register_beforeAddition_atComplexContent(addition, index);
    }

    private void register_beforeAddition_atComplexContent(T addition, int index) {
        complexContent.forEach((predicate, lookupTable) -> {
            if (predicate.test(addition)) {
                lookupTable.register(table.rawLinesView().get(index));
            }
        });
    }

    @Override
    public void register_removal(T removal, int index) {
        final var line = table.rawLinesView().get(index);
        content.get(removal).removeRegistration(line);
        // garbage collection
        if (content.get(removal).isEmpty()) {
            content.remove(removal);
        }
        complexContent.forEach((predicate, lookupTable) -> {
            if (predicate.test(removal)) {
                lookupTable.removeRegistration(table.rawLinesView().get(index));
            }
        });
    }

    @Override
    public Table lookup(T value) {
        if (content.containsKey(value)) {
            return content.get(value);
        }
        return lookupTable;
    }

    @Override
    public Table lookup(Predicate<T> predicate) {
        if (!complexContent.containsKey(predicate)) {
            final var lookup = LookupTable.lookupTable(table, predicate.toString());
            complexContent.put(predicate, lookup);
            table
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
        final List<String> path = table.path();
        path.add(attribute.name());
        return path;
    }
}
