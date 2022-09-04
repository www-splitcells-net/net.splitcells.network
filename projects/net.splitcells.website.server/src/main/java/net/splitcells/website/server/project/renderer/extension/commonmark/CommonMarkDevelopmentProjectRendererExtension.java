/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.project.renderer.extension.commonmark;

import net.splitcells.website.server.project.renderer.extension.ProjectRendererExtension;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.website.server.project.renderer.extension.commonmark.RootFileProjectRendererExtension.rootFileProjectRendererExtension;

public class CommonMarkDevelopmentProjectRendererExtension {

    public static ProjectRendererExtension commonMarkDevelopmentRenderer() {
        return rootFileProjectRendererExtension("DEVELOPMENT");
    }

    private CommonMarkDevelopmentProjectRendererExtension() {
        throw constructorIllegal();
    }
}
