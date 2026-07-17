/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.parser;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.source.geal.GealLexer;
import net.splitcells.dem.source.geal.GealParser.Source_unitContext;
import net.splitcells.dem.utils.ExecutionException;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.AntlrUtils.baseErrorListener;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

@JavaLegacy
public class GealAntlrUtils {

    public static Source_unitContext parseSourceUnit(String arg) {
        final var lexer = new GealLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.source.geal.GealParser(new CommonTokenStream(lexer));
        final List<Tree> parsingErrors = list();
        parser.addErrorListener(baseErrorListener(parsingErrors));
        final var sourceUnit = parser.source_unit();
        if (parsingErrors.hasElements()) {
            throw ExecutionException.execException(parsingErrors);
        }
        return sourceUnit;
    }

    private GealAntlrUtils() {
        throw constructorIllegal();
    }
}
