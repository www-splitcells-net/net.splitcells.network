/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.benchmark;

import lombok.val;
import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;

import java.util.Collection;
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

    public static void requireImplRuntimeOrder(Collection<RunResult> runResults, String... impls) {
        val sortedImplRuns = Lists.<RunResult>list();
        for (String impl : impls) {
            val matches = runResults.stream()
                    .filter(r -> impl.equals(r.getParams().getParam("impl")))
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
