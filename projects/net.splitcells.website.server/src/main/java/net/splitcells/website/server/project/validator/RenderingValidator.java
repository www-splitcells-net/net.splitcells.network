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
package net.splitcells.website.server.project.validator;

import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.processor.BinaryMessage;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Validates results rendered by {@link net.splitcells.website.server.project.Renderer#render}.
 */
@FunctionalInterface
public interface RenderingValidator {
    boolean validate(Optional<BinaryMessage> content, ProjectsRendererI projectsRendererI, Path requestedPath);

    default void startReport(String context) {

    }
    
    default void endReport() {

    }
}
