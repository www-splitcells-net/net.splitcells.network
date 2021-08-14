/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.splitcells.dem.Dem.environment;

/**
 * IDEA Only use target folder during development.
 */
public class ProcessPath extends OptionI<Path> {
    public ProcessPath() {
        super(() ->
                environment().config().configValue(ProcessHostPath.class)
                        .resolve(Paths.get("."
                                , environment().config().configValue(ProgramName.class).split("\\."))));
    }
}