/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.Service;


/**
 * <p>If set to false, during the {@link Environment#start()} all configured {@link Service} are not started.</p>
 * <p>This is done, in order to be able to start a distro project without needing to open a new port,
 * which in return simplifies the rendering of the static website.
 * In other words, {@link Service} requires resources, that sometimes are not required for a certain use case.
 * This can simplify the debugging via an IDE as well.</p>
 */
public class StartServicesAutomatically implements Option<Boolean> {
    @Override public Boolean defaultValue() {
        return true;
    }
}
