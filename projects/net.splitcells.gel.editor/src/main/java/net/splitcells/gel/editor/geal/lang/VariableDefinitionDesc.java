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

import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class VariableDefinitionDesc implements StatementDesc {
    public static VariableDefinitionDesc variableDefinitionDesc(NameDesc name, ExpressionDesc expression) {
        return variableDefinitionDesc(name, FunctionCallChainDesc.functionCallChainDescForTest(expression), emptySourceCodeQuote());
    }

    public static VariableDefinitionDesc variableDefinitionDesc(NameDesc name, FunctionCallChainDesc functionCallChain) {
        return variableDefinitionDesc(name, functionCallChain, emptySourceCodeQuote());
    }

    public static VariableDefinitionDesc variableDefinitionDesc(NameDesc name, FunctionCallChainDesc functionCallChain, SourceCodeQuote quote) {
        return new VariableDefinitionDesc(name, functionCallChain, quote);
    }

    @Getter private final NameDesc name;
    @Getter private final FunctionCallChainDesc functionCallChain;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private VariableDefinitionDesc(NameDesc argName, FunctionCallChainDesc argFunctionCallChain, SourceCodeQuote quote) {
        name = argName;
        functionCallChain = argFunctionCallChain;
        sourceCodeQuote = quote;
    }
}
