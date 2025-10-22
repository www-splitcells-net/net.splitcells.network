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

public interface FunctionCallRunner {

    /**
     * TODO In the future a {@link FunctionCallDesc} parser has to be used,
     * that standardizes error messages and makes it possible to generate a function documentation via instances of this parser.
     *
     * @param functionCall
     * @param subject
     * @param context
     * @return
     */
    FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context);

    /**
     * This makes it possible for the {@link FunctionCallRunner}
     * to create a documentation for function calls being supported to be run.
     *
     * @return
     */
    List<FunctionCallRunnerParser<?>> parsers();
}
