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

import net.splitcells.dem.environment.config.framework.OptionI;

import java.nio.file.Path;

import static net.splitcells.dem.resource.host.SystemUtils.runShellScript;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class HostName extends OptionI<String> {
    public HostName() {
        super(() -> {
            final var hostname = runShellScript("hostname", Path.of("."));
            if (hostname.exitCode() != 0) {
                throw executionException("Could not determine hostname: " + hostname.output());
            }
            return hostname.output();
        });
    }
}
