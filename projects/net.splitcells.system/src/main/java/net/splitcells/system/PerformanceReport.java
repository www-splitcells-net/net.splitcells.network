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
package net.splitcells.system;

import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import static net.splitcells.dem.resource.Trail.trail;

/**
 * Provides a page, that provides statistics of different counters,
 * in order to indicate the runtime and memory performance of the problem.
 * It is intended to be used for experiments,
 * where the program is started with just the experiment code being active.
 */
public class PerformanceReport implements ProjectsRendererExtension {
    public static ProjectsRendererExtension performanceReport() {
        return new PerformanceReport();
    }
    private static final Trail PATH = trail("/net/splitcells/system/performance-report.html");
    private PerformanceReport() {

    }
    @Override
    public boolean requiresAuthentication(RenderRequest request) {
        return true;
    }
}
