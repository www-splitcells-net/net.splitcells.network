/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class ParserDocumentConfig {
    public static ParserDocumentConfig parserDocumentConfig() {
        return new ParserDocumentConfig();
    }

    @Getter @Setter private boolean renderVariation = false;

    private ParserDocumentConfig() {

    }
}
