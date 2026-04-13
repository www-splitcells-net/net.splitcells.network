/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config;

import net.splitcells.dem.environment.config.framework.Option;

public class Initialized implements Option<Boolean> {
    @Override public Boolean defaultValue() {
        return false;
    }
}
