/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.benchmark;

import lombok.val;
import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.benchmark.SetBenchmark;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class JmhHelper {

    private JmhHelper() {
        throw constructorIllegal();
    }

    /**
     *
     * @param clazz      Benchmarks this given class. Only one test with a parametrized state is allowed.
     * @param testMethod The Method annotated with {@link Param}, that is used for the benchmark.
     * @param impls      The "impls" parameter values. The first element should be the fastest and
     *                   the last element should be the slowest.
     */
    public static void requireImplRuntimeOrder(String testMethod, Class<?> clazz, String... impls) {
        try {
            val test = clazz.getName() + "." + testMethod;
            val opt = new OptionsBuilder()
                    .include(test)
                    .warmupIterations(3)
                    .warmupTime(TimeValue.seconds(2))
                    .measurementIterations(5)
                    .measurementTime(TimeValue.seconds(3))
                    .forks(1)
                    .mode(Mode.Throughput)
                    .timeUnit(TimeUnit.SECONDS)
                    .shouldDoGC(true)
                    .addProfiler(GCProfiler.class)
                    .build();
            requireImplRuntimeOrder(new Runner(opt).run(), test, impls);
        } catch (RunnerException e) {
            throw execException(e);
        }
    }

    private static void requireImplRuntimeOrder(Collection<RunResult> runResults, String test, String... impls) {
        val sortedImplRuns = Lists.<RunResult>list();
        if (runResults.isEmpty()) {
            throw execException("There are no " + RunResult.class.getSimpleName() + " produced by a benchmark run. This seems to be incorrect.");
        }
        for (String impl : impls) {
            val matches = runResults.stream()
                    .filter(r -> r.getParams().getBenchmark().endsWith(test)
                            && impl.equals(r.getParams().getParam("impl")))
                    .toList();
            if (matches.size() != 1) {
                throw execException("There should be one "
                        + RunResult.class.getName()
                        + " with the implementation "
                        + impl
                        + " but "
                        + matches.size()
                        + " are present instead: "
                        + matches);
            }
            sortedImplRuns.add(matches.get(0));
        }
        requireImplRuntimeOrder(sortedImplRuns);
    }

    /**
     * Asserts that all results' runtime are in the given order.
     * This way performance assumptions and requirements are checked.
     *
     * @param results All runs with different parameters, in ascending runtime order.
     *                In other words, the first result should be the fastest and the last result should the slowest result.
     *                All {@link RunResult} requires an "impl" in {@link RunResult#getParams()}.
     *                In practical terms, the {@link RunResult#getPrimaryResult()} {@link Result#getScore()}
     *                should be highest for the first result and lowest for the last result.
     *
     */
    private static void requireImplRuntimeOrder(List<RunResult> results) {
        range(0, results.size() - 1).forEach(i ->
                describedBool(results.get(i).getPrimaryResult().getScore() > results.get(i + 1).getPrimaryResult().getScore()
                        , () -> "The implementation "
                                + results.get(i).getParams().getParam("impl")
                                + " (="
                                + results.get(i).getPrimaryResult().getScore()
                                + ") should be faster (higher score) than "
                                + results.get(i + 1).getParams().getParam("impl")
                                + " (="
                                + results.get(i + 1).getPrimaryResult().getScore()
                                + "), but is not."
                ).required());
    }
}
