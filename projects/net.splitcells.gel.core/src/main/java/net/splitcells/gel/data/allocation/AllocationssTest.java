/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.allocation;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.object.Discoverable.NO_CONTEXT;
import static net.splitcells.dem.testing.Assertions.requireIllegalDefaultConstructor;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

public class AllocationssTest {
    @UnitTest
    public void testIllegalConstructor() {
        requireIllegalDefaultConstructor(Allocationss.class);
    }

    @UnitTest
    public void testHeader() {
        final var attribute1 = attribute(Integer.class, "1");
        final var attribute2 = attribute(Integer.class, "2");
        final var attribute3 = attribute(Integer.class, "3");
        final var attribute4 = attribute(Integer.class, "4");
        final var testSubject = allocations("testHeader", NO_CONTEXT
                , list(attribute1, attribute2), list(attribute3, attribute4));
        testSubject.headerView().requireEqualityTo(list(attribute1, attribute2, attribute3, attribute4));
    }
    
    public void testAllocations() {
        final var attribute1 = attribute(Integer.class, "1");
        final var attribute2 = attribute(Integer.class, "2");
        final var testSubject = allocations("testAllocations", NO_CONTEXT
                , list(attribute1), list(attribute2));
        testSubject.demands().addTranslated(list(1));
        testSubject.supplies().addTranslated(list(1));
        testSubject.allocate(testSubject.demands().orderedLine(0)
                , testSubject.supplies().orderedLine(0));
    }
}
