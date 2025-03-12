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

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.antlr.v4.runtime.ParserRuleContext;

import static net.splitcells.dem.source.SourceUtils.root;

@JavaLegacy
public class SourceCodeQuote {
    public static SourceCodeQuote emptySourceCodeQuote() {
        return sourceCodeQuote("", "", 0);
    }

    public static SourceCodeQuote sourceCodeQuote(ParserRuleContext arg) {
        return new SourceCodeQuote(arg.getText(), root(arg).getText(), arg.getStart().getLine());
    }

    public static SourceCodeQuote sourceCodeQuote(String quote, String document, int quoteLine) {
        return new SourceCodeQuote(quote, document, quoteLine);
    }

    private final String quote;
    private final String document;
    private final int quoteLine;

    private SourceCodeQuote(String argQuote, String argDocument, int argQuoteLine) {
        quote = argQuote;
        document = argDocument;
        quoteLine = argQuoteLine;
    }

    public String quote() {
        return quote;
    }

    public String document() {
        return document;
    }

    public int quoteLine() {
        return quoteLine;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof SourceCodeQuote other) {
            return quote.equals(other.quote()) && document.equals(other.document()) && quoteLine == other.quoteLine();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Thing.hashCode(quote, document, quoteLine);
    }

    @Override
    public String toString() {
        return "quote: " + quote + ", document: " + document + ", quoteLine: " + quoteLine;
    }
}
