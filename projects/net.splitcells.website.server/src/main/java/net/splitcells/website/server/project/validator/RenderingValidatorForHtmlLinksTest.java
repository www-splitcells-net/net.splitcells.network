/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.validator;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.dem.data.atom.Bools.requireNot;
import static net.splitcells.website.server.project.validator.RenderingValidatorForHtmlLinks.PATH;

public class RenderingValidatorForHtmlLinksTest {
    @UnitTest
    public void testPathRegex() {
        require(PATH.matcher("./abc/abc").matches());
        require(PATH.matcher("./Abc/aBc").matches());
        require(PATH.matcher("/abC/Abc").matches());
        require(PATH.matcher("aBc/abC").matches());
        require(PATH.matcher("Abc").matches());
        require(PATH.matcher("Abc-abC").matches());
        requireNot(PATH.matcher("abc,abc").matches());
        requireNot(PATH.matcher("./abc/").matches());
    }
}
