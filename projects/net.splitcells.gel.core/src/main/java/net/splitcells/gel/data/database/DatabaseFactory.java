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
package net.splitcells.gel.data.database;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.AspectOrientedResource;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.gel.data.table.attribute.Attribute;
import org.w3c.dom.Element;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.lang.Xml.directChildElementByName;
import static net.splitcells.dem.lang.Xml.directChildElementsByName;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;

public interface DatabaseFactory extends AspectOrientedResource<Database>, ConnectingConstructor<Database> {
    Database database(String name, Attribute<? extends Object>... attributes);

    Database database(Attribute<? extends Object>... attributes);

    @Deprecated
    default Database database_(List<Attribute<?>> attributes) {
        return database(attributes);
    }

    Database database(List<Attribute<?>> attributes);

    Database database2(String name, Discoverable parent, List<Attribute<Object>> attributes);

    Database database(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues);

    @Deprecated
    Database database(String name, Discoverable parent, Attribute<? extends Object>... attributes);

    Database database(String name, Discoverable parent, List<Attribute<? extends Object>> attributes);

    default Database databaseOfFods(List<Attribute<?>> attributes, Element fods) {
        final var databaseOfFods = database(attributes);
        final var body = directChildElementByName(fods, "body", FODS_OFFICE);
        final var speardsheet = directChildElementByName(body, "spreadsheet", FODS_OFFICE);
        final var table = directChildElementByName(speardsheet, "table", FODS_TABLE);
        directChildElementsByName(table, "table-row", FODS_TABLE)
                .skip(1)
                .map(row -> lineOfFodsRow(attributes, row))
                .forEach(rindasVērtības -> databaseOfFods.addTranslated(rindasVērtības));
        return databaseOfFods;
    }

    private static List<Object> lineOfFodsRow(List<Attribute<?>> attributes, Element row) {
        final var tableCells = directChildElementsByName(row, "table-cell", FODS_TABLE)
                .collect(toList());
        return range(0, attributes.size())
                .mapToObj(i -> attributes.get(i).deserializeValue(
                        Xml.directChildElements(tableCells.get(i))
                                .filter(e -> FODS_TEXT.uri().equals(e.getNamespaceURI()))
                                .findFirst()
                                .get()
                                .getTextContent()))
                .collect(toList());
    }
}
