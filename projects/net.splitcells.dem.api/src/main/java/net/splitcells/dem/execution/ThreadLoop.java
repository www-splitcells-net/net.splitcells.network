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
package net.splitcells.dem.execution;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.dem.resource.communication.log.LogLevel;

import static net.splitcells.dem.Dem.executeThread;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.communication.log.Logs.logs;

@JavaLegacy
public class ThreadLoop {

    public static ThreadLoop threadLoop(Class<?> representer, Runnable loopStep) {
        return new ThreadLoop(representer.getName(), loopStep);
    }
    public static ThreadLoop threadLoop(String name, Runnable loopStep) {
        return new ThreadLoop(name, loopStep);
    }

    private volatile boolean isRunning = true;

    private ThreadLoop(String name, Runnable loopStep) {
        executeThread(name, () -> {
            while (isRunning) {
                loopStep.run();
            }
            logs().append(tree("Stopping thread loop").withProperty("name", name), LogLevel.INFO);
        });
    }

    public void stop() {
        isRunning = false;
    }
}
