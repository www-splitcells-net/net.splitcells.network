/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.testing.Assertions;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.dem.data.atom.Integers.requireEqualInts;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.StringUtils.*;

public class StringUtilsTest {

    @Test
    public void testIntToOrdinal() {
        requireEquals(intToOrdinal(1), "1st");
        requireEquals(intToOrdinal(2), "2nd");
        requireEquals(intToOrdinal(3), "3rd");
        requireEquals(intToOrdinal(4), "4th");
        Assertions.requireThrow(() -> intToOrdinal(0));
        Assertions.requireThrow(() -> intToOrdinal(-1));
    }

    @Test
    public void testCountChar() {
        requireEqualInts(1, countChar("abcdefg", 'a'));
        requireEqualInts(2, countChar("abcdefga", 'a'));
        requireEqualInts(2, countChar("aabcdefg", 'a'));
        requireEqualInts(0, countChar("bcdefg", 'a'));
    }

    @Test
    public void testMultiple() {
        requireEquals(multiple("23", 3), "232323");
    }

    @Test
    public void testParseInt() {
        Assertions.requireEquals(StringUtils.parseInt("123"), 123);
    }

    @Test
    public void testIsInt() {
        require(StringUtils.isInt("123"));
        requireNot(StringUtils.isInt("++123"));
    }
}
