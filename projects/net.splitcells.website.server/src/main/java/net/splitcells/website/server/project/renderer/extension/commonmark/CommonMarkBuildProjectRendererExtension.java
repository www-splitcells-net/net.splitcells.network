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
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.website.server.project.renderer.extension.commonmark.RootFileProjectRendererExtension.rootFileProjectRendererExtension;

public class CommonMarkBuildProjectRendererExtension {

    public static ProjectRendererExtension commonMarkBuildRenderer() {
        return rootFileProjectRendererExtension("BUILD");
    }

    private CommonMarkBuildProjectRendererExtension() {
        throw constructorIllegal();
    }
}
