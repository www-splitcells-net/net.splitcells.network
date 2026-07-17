/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.resource;

/**
 * States how many CPU processors of the hardware are available for Java.
 */
public class HostHardware {
    private HostHardware() {

    }

    public static int cpuCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
