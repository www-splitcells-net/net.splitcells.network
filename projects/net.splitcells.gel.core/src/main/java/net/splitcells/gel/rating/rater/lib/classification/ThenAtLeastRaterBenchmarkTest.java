/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib.classification;

import lombok.val;
import net.splitcells.dem.testing.annotations.BenchmarkTest;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.infra.Blackhole;

import java.util.function.Supplier;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.benchmark.JmhHelper.requireImplRuntimeOrder;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineValue.lineValueRater;

public class ThenAtLeastRaterBenchmarkTest {

    @BenchmarkTest public void test() {
        requireImplRuntimeOrder("test", getClass(), "fast", "correct");
    }

    @Benchmark public void test(BenchmarkState state, Blackhole blackhole) {
        range(0, 10).forEach(i -> blackhole.consume(state.test.get()));
    }

    @State(Scope.Thread) public static class BenchmarkState {
        private Supplier<Object> test;
        @Param({"correct", "fast"}) private String impl;

        @Setup(Level.Invocation) public void setupIteration() {
            test = switch (impl) {
                case "correct" -> () -> {
                    val tester = new ThenAtLeastRaterTest();
                    tester.testRating();
                    return tester;
                };
                case "fast" -> () -> {
                    val tester = new ThenAtLeastFastRaterTest();
                    tester.testRating();
                    return tester;
                };
                default -> throw execException(impl);
            };
        }
    }
}
