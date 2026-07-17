/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
     * <p>TODO Use a single config object as an argument, to make arguments extendable.</p>
     * <p>TODO Provide a parser utility object, that is used to detect,
     * if the given {@link FunctionCallDesc} is supported by this.</p>
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
