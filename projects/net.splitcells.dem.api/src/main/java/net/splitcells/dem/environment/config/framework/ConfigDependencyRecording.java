/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.Dem;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.environment.config.framework.ConfigDependencyRecorder.dependencyRecorder;
import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * Records all {@link Option} dependencies for one given {@link Dem#process(Runnable)}.
 */
public class ConfigDependencyRecording implements Option<ConfigDependencyRecorder> {
    @Override
    public ConfigDependencyRecorder defaultValue() {
        return dependencyRecorder();
    }

    @Override public Optional<Tree> serialize(ConfigDependencyRecorder currentValue) {
        return Optional.empty();
    }
}
