/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.environment.config.framework.ConfigurationV;
import net.splitcells.dem.resource.communication.Closeable;

/**
 * TODO Implement ExecutorService.
 * 
 * TODO Implement Stacking and sandboxing.
 */
public interface EnvironmentV {

    Configuration config();
}