/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.test;

import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.HostHardware;

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
    @Override
    public Integer defaultValue() {
        return HostHardware.cpuCoreCount();
    }
}
