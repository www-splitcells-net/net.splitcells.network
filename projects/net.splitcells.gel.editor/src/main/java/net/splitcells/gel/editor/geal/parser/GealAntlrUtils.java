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
package net.splitcells.gel.editor.geal.parser;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.source.geal.GealLexer;
import net.splitcells.dem.source.geal.GealParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.AntlrUtils.baseErrorListener;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class GealAntlrUtils {

    public static GealParser gealParser(String arg) {
        final var lexer = new GealLexer(CharStreams.fromString(arg));
        final var parser = new net.splitcells.dem.source.geal.GealParser(new CommonTokenStream(lexer));
        final List<Tree> parsingErrors = list();
        parser.addErrorListener(baseErrorListener(parsingErrors));
        if (parsingErrors.hasElements()) {
            final var errorReport = tree("Parsing Error Report");
            parsingErrors.forEach(errorReport::withChild);
            throw execException(errorReport);
        }
        return parser;
    }

    private GealAntlrUtils() {
        throw constructorIllegal();
    }
}
