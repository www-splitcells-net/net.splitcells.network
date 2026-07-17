/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication.log;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoggerLevelTest {
    @Test
    public void testPrioritySmallerThanOrder() {
        assertThat(LogLevel.ERROR.smallerThan(LogLevel.DEBUG)).isFalse();
        assertThat(LogLevel.DEBUG.smallerThan(LogLevel.DEBUG)).isFalse();
        assertThat(LogLevel.DEBUG.smallerThan(LogLevel.ERROR)).isTrue();
    }
}
