/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
