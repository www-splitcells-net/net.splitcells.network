/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal;

import lombok.Getter;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class StringDesc implements ExpressionDesc {
    public static StringDesc stringDesc(String value) {
        return new StringDesc(value, emptySourceCodeQuote());
    }

    public static StringDesc stringDesc(String value, SourceCodeQuote sourceCodeQuote) {
        return new StringDesc(value, sourceCodeQuote);
    }

    @Getter private final String value;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private StringDesc(String argValue, SourceCodeQuote argSourceCodeQuote) {
        value = argValue;
        sourceCodeQuote = argSourceCodeQuote;
    }
}
