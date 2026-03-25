/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import net.splitcells.dem.testing.annotations.BenchmarkTest;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.object.Discoverable.EXPLICIT_NO_CONTEXT;
import static net.splitcells.dem.testing.benchmark.JmhHelper.benchmark;
import static net.splitcells.gel.data.table.Tables.table;
import static net.splitcells.gel.data.view.attribute.AttributeI.attribute;

public class TableBenchmarkTest {
    @BenchmarkTest public void test() {
        benchmark(TableBenchmarkTest.class);
    }

    @Benchmark public void testAddRemove(Blackhole blackhole) {
        final var testSubject = table("test-subject", EXPLICIT_NO_CONTEXT, list(attribute(Integer.class)));
        range(0, 1000).forEach(testSubject::addTranslated);
        range(0, 1000).forEach(testSubject::remove);
        blackhole.consume(testSubject);
    }
}
