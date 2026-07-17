/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.environment.config.framework.Variable.variable;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class VariableTest {
    @Test
    public void test() {
        final Variable<Integer> testSubject = variable();
        require(testSubject.value().isEmpty());
        testSubject.withValue(Optional.of(1));
        requireEquals(testSubject.value().orElseThrow(), 1);
    }
}
