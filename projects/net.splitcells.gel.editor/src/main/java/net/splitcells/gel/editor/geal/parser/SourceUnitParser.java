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
import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.source.geal.GealParser;
import net.splitcells.gel.editor.geal.lang.SourceUnit;
import net.splitcells.gel.editor.geal.lang.StatementDesc;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.gel.editor.geal.parser.GealAntlrUtils.parseSourceUnit;
import static net.splitcells.gel.editor.geal.parser.StatementParser.parseStatement;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.sourceCodeQuote;

@JavaLegacy
public class SourceUnitParser extends net.splitcells.dem.source.geal.GealParserBaseVisitor<SourceUnit> {

    public static SourceUnit parseGealSourceUnit(String arg) {
        return new SourceUnitParser().visitSource_unit(parseSourceUnit(arg));
    }

    private SourceUnitParser() {
    }

    @Override
    public SourceUnit visitSource_unit(GealParser.Source_unitContext ctx) {
        final List<StatementDesc> statements = list();
        ctx.statement().forEach(s -> {
            statements.add(parseStatement(s));
        });
        return SourceUnit.sourceUnit(statements, sourceCodeQuote(ctx));
    }
}
