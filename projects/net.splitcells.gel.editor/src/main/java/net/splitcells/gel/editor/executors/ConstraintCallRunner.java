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
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.lang.geal.FunctionCallDesc;

import java.util.Optional;

import static net.splitcells.dem.data.atom.Bools.require;
import static net.splitcells.gel.editor.EditorParser.CONSTRAINT_FUNCTION;

public class ConstraintCallRunner implements FunctionCallRunner {
    public static ConstraintCallRunner constraintCallRunner() {
        return new ConstraintCallRunner();
    }

    private @Setter Optional<Editor> context = Optional.empty();
    private @Setter Optional<Object> subject = Optional.empty();
    private @Getter Optional<Object> result = Optional.empty();

    private ConstraintCallRunner() {

    }

    @Override
    public boolean supports(FunctionCallDesc functionCall) {
        return functionCall.getName().getValue().equals(CONSTRAINT_FUNCTION)
                && functionCall.getArguments().isEmpty();
    }

    @Override
    public FunctionCallRunner execute(FunctionCallDesc functionCall) {
        require(supports(functionCall));
        this.result = Optional.of(ForAlls.forAll());
        return this;
    }

    @Override
    public FunctionCallRunner setSubject(Optional<Object> argSubject) {
        subject = argSubject;
        return this;
    }

    @Override
    public FunctionCallRunner setContext(Optional<Editor> argContext) {
        context = argContext;
        return this;
    }
}
