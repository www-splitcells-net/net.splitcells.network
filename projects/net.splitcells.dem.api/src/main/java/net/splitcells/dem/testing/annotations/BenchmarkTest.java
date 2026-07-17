/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.testing.annotations;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.testing.TestTypes;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>TODO Create guidelines and goals of benchmarks.</p>
 * <p>TODO Create measurement tests in the future,
 * that only measure things and where the data is tested by a dedicated analyzer.
 * Such measurement tests could register such analyzer routines to the dedicated analyzer.</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag(TestTypes.BENCHMARK_RUNTIME)
@Test
@JavaLegacy
public @interface BenchmarkTest {

}
