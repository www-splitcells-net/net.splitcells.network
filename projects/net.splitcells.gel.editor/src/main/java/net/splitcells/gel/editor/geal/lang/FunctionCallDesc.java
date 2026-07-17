/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.lang;

import lombok.Getter;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.lang.SourceCodeQuote;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.lang.SourceCodeQuote.emptySourceCodeQuote;

public final class FunctionCallDesc implements ExpressionDesc {
    public static FunctionCallDesc functionCallDescForTest(String name) {
        return functionCallDesc(NameDesc.nameDescForTest(name), list(), emptySourceCodeQuote());
    }

    public static FunctionCallDesc functionCallDescForTest(NameDesc name) {
        return functionCallDesc(name, list(), emptySourceCodeQuote());
    }

    public static FunctionCallDesc functionCallDescForTest(NameDesc name, List<ExpressionDesc> arguments) {
        return functionCallDesc(name, arguments.mapped(e -> FunctionCallChainDesc.functionCallChainDescForTest(e)), emptySourceCodeQuote());
    }

    public static FunctionCallDesc functionCallDescForTest2(NameDesc name, List<FunctionCallChainDesc> arguments) {
        return functionCallDesc(name, arguments, emptySourceCodeQuote());
    }

    public static FunctionCallDesc functionCallDesc(NameDesc name, List<FunctionCallChainDesc> arguments, SourceCodeQuote sourceCodeQuote) {
        return new FunctionCallDesc(name, arguments, sourceCodeQuote);
    }

    public static FunctionCallDesc functionCallDesc2(NameDesc name, List<ExpressionDesc> arguments, SourceCodeQuote sourceCodeQuote) {
        return new FunctionCallDesc(name, arguments.mapped(e -> FunctionCallChainDesc.functionCallChainDescForTest(e)), sourceCodeQuote);
    }

    @Getter private final NameDesc name;
    @Getter private final List<FunctionCallChainDesc> arguments;
    @Getter private final SourceCodeQuote sourceCodeQuote;

    private FunctionCallDesc(NameDesc argName, List<FunctionCallChainDesc> argArguments, SourceCodeQuote argSourceCodeQuote) {
        name = argName;
        arguments = argArguments;
        sourceCodeQuote = argSourceCodeQuote;
    }

    @Override
    public String toString() {
        return name.getValue();
    }
}
