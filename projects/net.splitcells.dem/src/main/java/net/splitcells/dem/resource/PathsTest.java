/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JavaLegacy
public class PathsTest {
    @Test
    public void testRemoveFileSuffix() {
        assertThat(Paths.removeFileSuffix("name.suffix")).isEqualTo("name");
    }
}
