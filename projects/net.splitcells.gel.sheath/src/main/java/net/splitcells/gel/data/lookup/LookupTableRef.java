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
package net.splitcells.gel.data.lookup;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import org.w3c.dom.Element;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.environment.config.StaticFlags.TRACING;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.resource.communication.interaction.LogLevel.DEBUG;
import static org.assertj.core.api.Assertions.assertThat;

public class LookupTableRef extends LookupTable {
    protected LookupTableRef(Table table, String name) {
        super(table, name);
    }

    /**
     * TODO Check if types of return value and {@link T} fit each other.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Column<T> columnView(Attribute<T> attribute) {
        return super.columnView(attribute);
    }

    /**
     * The indexes needs to be preserved and therefore the null value gaps needs to
     * be added as well.
     * <p/>
     * TODO PERFORMANCE
     */
    @Override
    public List<Line> rawLinesView() {
        if (ENFORCING_UNIT_CONSISTENCY) {
            range(0, tableView.rawLinesView().size()).forEach(i -> {
                if (content.contains(i)) {
                    assertThat(tableView.rawLinesView().get(i)).isNotNull();
                } else {
                    assertThat(content).doesNotContain(i);
                }
            });
        }
        return super.rawLinesView();
    }

    /**
     * TODO RENAME
     */
    @Override
    public void register(Line line) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assert !content.contains(line.index());
        }
        if (TRACING) {
            domsole().append(Xml.elementWithChildren("register.LookupTable"
                    , Xml.elementWithChildren("subject", textNode(path().toString()))
                    , line.toDom()
                    )
                    , this, DEBUG);
        }
        super.register(line);
    }

    /**
     * TODO RENAME
     */
    @Override
    public void removeRegistration(Line line) {
        if (TRACING) {
            domsole().append(Xml.elementWithChildren("deregister." + getClass().getSimpleName()
                    , Xml.elementWithChildren("subject", textNode(path().toString()))
                    , Xml.elementWithChildren("content", textNode(content.toString()))
                    , line.toDom()
                    )
                    , this, DEBUG);
        }
        if (ENFORCING_UNIT_CONSISTENCY) {
            assert content.contains(line.index());
        }
        super.removeRegistration(line);
        if (TRACING) {
            domsole().append(
                    Xml.elementWithChildren("after.deregister." + getClass().getSimpleName()
                            , Xml.elementWithChildren("subject", textNode(path().toString()))
                            , Xml.elementWithChildren("content", textNode(content.toString()))
                            , line.toDom())
                    , this, DEBUG);
        }
    }

    @Override
    public Element toDom() {
        final var dom = super.toDom();
        content.forEach(i -> {
            if (ENFORCING_UNIT_CONSISTENCY) {
                assertThat(rawLinesView().get(i)).isNotNull();
            }
        });
        return dom;
    }
}
