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
package net.splitcells.gel.editor.lang;

public final class StringDescription implements ArgumentDescription, SourceCodeQuotation {
    public static StringDescription stringDescription(String value, SourceCodeQuote sourceCodeQuote) {
        return new StringDescription(value, sourceCodeQuote);
    }

    private final String value;
    private final SourceCodeQuote sourceCodeQuote;

    private StringDescription(String argValue, SourceCodeQuote argSourceCodeQuote) {
        value = argValue;
        sourceCodeQuote = argSourceCodeQuote;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value + " sourceCodeQuote: " + sourceCodeQuote;
    }

    @Override
    public SourceCodeQuote getSourceCodeQuote() {
        return sourceCodeQuote;
    }
}
