/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal;

import net.splitcells.dem.testing.annotations.DisabledTest;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.gel.editor.geal.FunctionCallDoc.generateDoc;

public class FunctionCallDocTest {
    @UnitTest
    public void testGenerateDoc() {
        generateDoc();
    }
}
