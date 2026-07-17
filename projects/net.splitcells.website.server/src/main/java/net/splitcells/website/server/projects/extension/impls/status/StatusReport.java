/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.projects.extension.impls.status;

import net.splitcells.dem.resource.communication.log.LogLevel;

public class StatusReport {

    public static StatusReport statusReport(LogLevel logLevel, String report) {
        return new StatusReport(logLevel, report);
    }

    private final LogLevel logLevel;
    private final String report;

    private StatusReport(LogLevel logLevel, String report) {
        this.logLevel = logLevel;
        this.report = report;
    }

    public LogLevel logLevel() {
        return logLevel;
    }

    public String report() {
        return report;
    }
}
