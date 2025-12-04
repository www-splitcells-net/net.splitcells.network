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
