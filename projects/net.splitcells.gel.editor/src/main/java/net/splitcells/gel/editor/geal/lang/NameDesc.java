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
package net.splitcells.gel.editor.geal.lang;

import lombok.Getter;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import java.util.regex.Pattern;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class NameDesc implements ExpressionDesc {
    public static NameDesc nameDescForTest(String value) {
        return new NameDesc(value, emptySourceCodeQuote());
    }

    public static NameDesc nameDesc(String value, SourceCodeQuote sourceCodeQuote) {
        return new NameDesc(value, sourceCodeQuote);
    }

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z_]++[a-zA-Z0-9_]*");
    @Getter private final String value;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private NameDesc(String argValue, SourceCodeQuote argSourceCodeQuote) {
        if (!PATTERN.matcher(argValue).matches()) {
            throw execException(tree("The name is invalid.").withProperty("value", argValue)
                    .withProperty("source code quote", argSourceCodeQuote.toString()));
        }
        value = argValue;
        sourceCodeQuote = argSourceCodeQuote;
    }
}
