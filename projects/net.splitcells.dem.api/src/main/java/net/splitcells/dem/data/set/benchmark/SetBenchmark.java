/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.benchmark;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.legacy.LegacySetEclipse.legacySetEclipse;
import static net.splitcells.dem.data.set.legacy.LegacySetJava.legacySetJava;
import static net.splitcells.dem.data.set.legacy.LegacySetTrove.legacySetTrove;
import static net.splitcells.dem.data.set.legacy.LegacySetWrapper.legacySetWrapper;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class SetBenchmark {

    public static void main(String... args) throws RunnerException {
        val opt = new OptionsBuilder()
                .include(SetBenchmark.class.getSimpleName())
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(3))
                .forks(1)
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.SECONDS)
                .shouldDoGC(true)
                .addProfiler(GCProfiler.class)
                .resultFormat(ResultFormatType.JSON)
                .build();
        val runner = new Runner(opt);
        val testResult = runner.run();
    }

    @Benchmark public void testRemoveAny(State state, Blackhole blackhole) {
        range(0, 100).forEach(i -> state.testSubject.removeAny());
        blackhole.consume(state.testSubject);
    }

    @org.openjdk.jmh.annotations.State(Scope.Thread)
    public static class State {
        public Set<Integer> testSubject;
        @Param({"Java", "Eclipse", "Trove"})
        public String impl;

        @Setup(Level.Invocation)
        public void setupIteration() {
            val factory = switch (impl) {
                case "Java" -> legacySetJava();
                case "Eclipse" -> legacySetEclipse();
                case "Trove" -> legacySetTrove();
                default -> throw execException(impl);
            };
            testSubject = legacySetWrapper(factory.legacySet());
            range(0, 100).forEach(testSubject::add);
        }
    }
}
