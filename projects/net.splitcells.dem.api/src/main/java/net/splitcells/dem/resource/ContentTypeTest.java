/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.XML;

public class ContentTypeTest {

    @UnitTest public void testParseOptionally() {
        Assertions.requireEquals(ContentType.parseOptionally("application/xml").orElseThrow(), XML);
        Assertions.requireEquals(ContentType.parseOptionally("invalid"), Optional.empty());
    }
}
