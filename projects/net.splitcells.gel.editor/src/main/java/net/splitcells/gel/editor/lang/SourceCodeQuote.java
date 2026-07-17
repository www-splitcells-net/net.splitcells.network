/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.lang;

import net.splitcells.dem.data.atom.Thing;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.source.SourceUtils.root;

@JavaLegacy
public class SourceCodeQuote {
    public static SourceCodeQuote emptySourceCodeQuote() {
        return sourceCodeQuote("", "", 0);
    }

    public static SourceCodeQuote sourceCodeQuote(ParserRuleContext arg) {
        return new SourceCodeQuote(arg.getText(), root(arg).getText(), arg.getStart().getLine());
    }

    public static SourceCodeQuote sourceCodeQuote(TerminalNode arg) {
        return new SourceCodeQuote(arg.getText(), arg.getText(), arg.getSymbol().getLine());
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

    /**
     * @return Provides a string, that describes in a short manner the quote's content and where it is located.
     */
    public String userReference() {
        return "quote: " + quote + ", quoteLine: " + quoteLine;
    }

    public Tree userReferenceTree() {
        return tree("Source code quote")
                .withProperty("quote", quote)
                .withProperty("quote line", "" + quoteLine);
    }

    public List<Tree> userReferenceTrees() {
        return list(tree("quote").withText(quote)
                , tree("quote line").withText("" + quoteLine));
    }
}
