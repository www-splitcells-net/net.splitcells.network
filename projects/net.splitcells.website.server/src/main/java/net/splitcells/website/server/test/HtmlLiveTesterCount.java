/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.test;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.HostHardware;
import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;

import static net.splitcells.dem.lang.tree.TreeI.tree;

/**
 * States, how many tests are run in simultaneously.
 */
public class HtmlLiveTesterCount implements Option<Integer> {
    /**
     * This value is set to {@link HostHardware#cpuCoreCount()}, in order to have a full CPU utilization by default.
     * Thereby, it is tested, that the program still works under kind of full load.
     *
     * @return
     */
    @Override public Integer defaultValue() {
        return HostHardware.cpuCoreCount();
    }

    /**
     * TODO This is an hack, because the value does not necessary correspond to the CPU core count of the host.
     * It can be also a decision to not utilize all CPU cores with the live testers as well.
     * 
     * @param currentValue
     * @return
     */
    @Override public Optional<Tree> serialize(Integer currentValue) {
        return Optional.of(tree("This config is queried from the host."));
    }
}
