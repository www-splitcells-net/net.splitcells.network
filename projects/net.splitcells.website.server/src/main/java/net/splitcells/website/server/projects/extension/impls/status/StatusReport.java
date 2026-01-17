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
