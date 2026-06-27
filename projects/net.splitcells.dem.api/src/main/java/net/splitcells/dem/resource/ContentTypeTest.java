/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.testing.annotations.UnitTest;

import java.util.Optional;

import static net.splitcells.dem.resource.ContentType.XML;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class ContentTypeTest {

    @UnitTest public void testParseOptionally() {
        requireEquals(ContentType.parseOptionally("application/xml").orElseThrow(), XML);
        requireEquals(ContentType.parseOptionally("invalid"), Optional.empty());
    }
}
