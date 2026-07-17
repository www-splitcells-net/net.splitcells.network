/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.host;

import net.splitcells.dem.environment.config.framework.OptionImpl;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.utils.ExecutionException;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.host.SystemUtils.runShellScript;
import static net.splitcells.dem.utils.ExecutionException.execException;

public class HostName extends OptionImpl<String> {
    public HostName() {
        super(() -> {
            final var hostname = runShellScript("hostname", Path.of("."));
            if (hostname.exitCode() != 0) {
                throw ExecutionException.execException("Could not determine hostname: " + hostname.output());
            }
            return hostname.output();
        });
    }
    @Override public Optional<Tree> serialize(String currentValue) {
        return Optional.of(tree(currentValue));
    }
}
