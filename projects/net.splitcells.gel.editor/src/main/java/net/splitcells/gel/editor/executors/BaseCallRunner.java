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
package net.splitcells.gel.editor.executors;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.gel.editor.executors.FunctionCallRun.functionCallRun;

@Accessors(chain = true)
public class BaseCallRunner implements FunctionCallRunner {

    public static BaseCallRunner baseCallRunner(BaseCallRunnerParser parser) {
        return new BaseCallRunner(parser);
    }

    private final BaseCallRunnerParser parser;
    @Setter @Getter private Optional<Editor> context = Optional.empty();
    @Setter @Getter private Optional<Object> subject = Optional.empty();
    @Setter @Getter private Optional<Object> result = Optional.empty();

    private BaseCallRunner(BaseCallRunnerParser argParser) {
        parser = argParser;
    }

    @Override
    public boolean supports(FunctionCallDesc functionCall) {
        return parser.supports(this, functionCall);
    }

    @Override
    public FunctionCallRun execute(FunctionCallDesc functionCall) {
        require(parser.supports(this, functionCall));
        parser.execute(this, functionCall);
        return functionCallRun(subject, context).setResult(result);
    }
}
