/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
