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
package net.splitcells.website.server;

import net.splitcells.dem.resource.host.Files;

import java.nio.file.Paths;

import static net.splitcells.website.Projects.projectsRenderer;

/**
 * TODO Make this an program argument.
 */
@Deprecated
public class StaticFileServer {
    public static void main(String... args) {
        final var target = Paths.get("target/public").toAbsolutePath();
        Files.createDirectory(target);
        projectsRenderer().serveTo(target);
    }
}
