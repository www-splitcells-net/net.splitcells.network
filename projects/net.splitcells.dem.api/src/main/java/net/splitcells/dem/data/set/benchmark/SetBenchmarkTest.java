/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.benchmark;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.testing.annotations.BenchmarkTest;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.legacy.LegacySetEclipseFactory.legacySetEclipseFactory;
import static net.splitcells.dem.data.set.legacy.LegacySetJava.legacySetJava;
import static net.splitcells.dem.data.set.legacy.LegacySetTrove.legacySetTrove;
import static net.splitcells.dem.data.set.legacy.LegacySetWrapper.legacySetWrapper;
import static net.splitcells.dem.testing.benchmark.JmhHelper.*;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class SetBenchmarkTest {

    @BenchmarkTest public void test() {
        requireImplRuntimeOrder("testRemoveAny", SetBenchmarkTest.class, "Eclipse", "Java", "Trove");
    }

    @Benchmark public void testRemoveAny(State state, Blackhole blackhole) {
        range(0, 100).forEach(i -> state.testSubject.removeAny());
        blackhole.consume(state.testSubject);
    }

    @org.openjdk.jmh.annotations.State(Scope.Thread)
    public static class State {
        private Set<Integer> testSubject;
        @Param({"Java", "Eclipse", "Trove"})
        private String impl;

        @Setup(Level.Invocation)
        public void setupIteration() {
            val factory = switch (impl) {
                case "Java" -> legacySetJava();
                case "Eclipse" -> legacySetEclipseFactory();
                case "Trove" -> legacySetTrove();
                default -> throw execException(impl);
            };
            testSubject = legacySetWrapper(factory.legacySet());
            range(0, 100).forEach(testSubject::add);
        }
    }
}
