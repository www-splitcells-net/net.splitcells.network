/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor.geal.runners;

import net.splitcells.dem.data.Flow;
import net.splitcells.gel.editor.Editor;
import net.splitcells.gel.editor.geal.lang.FunctionCallDesc;

import java.util.Optional;

public interface FunctionCallExecutor {

    FunctionCallRun execute(FunctionCallDesc functionCall, Optional<Object> subject, Editor context);

    Flow<FunctionCallRunnerParser<?>> parsers();
}
