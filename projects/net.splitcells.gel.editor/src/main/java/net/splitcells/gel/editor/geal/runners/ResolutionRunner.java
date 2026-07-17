/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.editor.EditorParser.INTEGER_TYPE;
import static net.splitcells.gel.editor.EditorParser.STRING_TYPE;
import static net.splitcells.gel.editor.geal.runners.FunctionCallRun.functionCallRun;
import static net.splitcells.gel.editor.geal.Type.type;

public class ResolutionRunner implements FunctionCallRunner {
    public static ResolutionRunner resolutionRunner() {
        return new ResolutionRunner();
    }

    private ResolutionRunner() {

    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context) {
        final var run = functionCallRun(subject, context);
        final var name = functionCall.getName().getValue();
        if (functionCall.getArguments().isEmpty()) {
            final var resolution = context.resolveRaw(functionCall.getName());
            if (resolution.isPresent()) {
                return run.setResult(resolution);
            }
            if (name.equals(STRING_TYPE)) {
                run.setResult(Optional.of(type(STRING_TYPE)));
            } else if (name.equals(INTEGER_TYPE)) {
                run.setResult(Optional.of(type(INTEGER_TYPE)));
            }
        }
        return run;
    }

    @Override
    public List<FunctionCallRunnerParser<?>> parsers() {
        return list();
    }
}
