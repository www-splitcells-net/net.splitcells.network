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

    /**
     * TODO REMOVE This method just makes {@link DatabaseFactory} unnecessary complex.
     *
     * @param attributes
     * @param linesValues
     * @return
     */
    @Deprecated
    Database database(List<Attribute<? extends Object>> attributes, List<List<Object>> linesValues);

    @Deprecated
    Database database(String name, Discoverable parent, Attribute<? extends Object>... attributes);

    Database database(String name, Discoverable parent, List<Attribute<? extends Object>> attributes);

    /* TODO Restore this unused method and create feature with it.
    default Database databaseOfFods(List<Attribute<?>> attributes, Element fods) {
        final var databaseOfFods = database(attributes);
        final var body = directChildElementByName(fods, "body", FODS_OFFICE);
        final var speardsheet = directChildElementByName(body, "spreadsheet", FODS_OFFICE);
        final var table = directChildElementByName(speardsheet, "table", FODS_TABLE);
        directChildElementsByName(table, "table-row", FODS_TABLE)
                .skip(1)
                .map(row -> lineOfFodsRow(attributes, row))
                .forEach(lineValue -> databaseOfFods.addTranslated(lineValue));
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
    }*/
}
