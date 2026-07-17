/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
