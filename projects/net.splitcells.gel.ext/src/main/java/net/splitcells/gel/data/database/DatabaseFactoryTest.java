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

import net.splitcells.dem.lang.Xml;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.reflection.ClassesRelated.resourceOfClass;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.table.attribute.AttributeI.stringAttribute;
import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseFactoryTest {
    /* TODO Restore this unused method and create feature with it.
    @Test
    public void testDatabaseOfFods() {
        final var testProduct = databaseOfFods(list
                        (integerAttribute("a")
                                , stringAttribute("b"))
                , Xml.parse(resourceOfClass(DatabaseFactoryTest.class, "database.example.fods"))
                        .getDocumentElement());
        assertThat(testProduct.unorderedLines().get(0).values()).isEqualTo(list(1, "2"));
        assertThat(testProduct.unorderedLines().get(1).values()).isEqualTo(list(2, "1"));
    }*/
}
