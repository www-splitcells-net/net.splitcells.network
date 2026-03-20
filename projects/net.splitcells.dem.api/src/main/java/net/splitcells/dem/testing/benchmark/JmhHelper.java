/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.benchmark;

import net.splitcells.dem.data.set.list.Lists;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.DescribedBool.describedBool;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class JmhHelper {
    private JmhHelper() {
        throw constructorIllegal();
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
    public static void requireImplRuntimeOrder(RunResult... results) {
        range(0, results.length - 1).forEach(i ->
                describedBool(results[i].getPrimaryResult().getScore() > results[i + 1].getPrimaryResult().getScore()
                        , () -> "The implementation "
                                + results[i].getParams().getParam("impl")
                                + " (="
                                + results[i].getPrimaryResult().getScore()
                                + ") should be faster (higher score) than "
                                + results[i + 1].getParams().getParam("impl")
                                + " (="
                                + results[i + 1].getPrimaryResult().getScore()
                                + "), but is not."
                ).required());
    }
}
