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

import net.splitcells.dem.lang.Xml;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.reflection.ClassesRelated.resourceOfClass;
import static net.splitcells.gel.data.database.Databases.databaseOfFods;
import static net.splitcells.gel.data.table.attribute.AttributeI.integerAttribute;
import static net.splitcells.gel.data.table.attribute.AttributeI.stringAttribute;
import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseFactoryTest {
    @Test
    public void testDatabaseOfFods() {
        final var testProduct = databaseOfFods(list
                        (integerAttribute("a")
                                , stringAttribute("b"))
                , Xml.parse(resourceOfClass(DatabaseFactoryTest.class, "database.example.fods"))
                        .getDocumentElement());
        assertThat(testProduct.getLines().get(0).values()).isEqualTo(list(1, "2"));
        assertThat(testProduct.getLines().get(1).values()).isEqualTo(list(2, "1"));
    }
}
