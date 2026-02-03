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

import net.splitcells.dem.lang.annotations.JavaLegacy;
import net.splitcells.website.server.project.validator.RenderingValidator;
import net.splitcells.website.server.projects.ProjectsRenderer;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.commonmark.node.Link;

import java.nio.file.Path;
import java.util.regex.Pattern;

import static net.splitcells.dem.resource.Trail.elementCount;
import static net.splitcells.dem.resource.Trail.parentCount;
import static net.splitcells.dem.resource.Trail.withoutPrefixElements;
import static net.splitcells.dem.resource.Trail.withoutSuffixElements;

/**
 * <p>This class translates links inside CommonMark documents.
 * It assumes, that relative links are all relative to the documents position
 * inside the enclosing project's source code.
 * It is assumed, that the enclosing project's file structure is similar
 * to this project's source code repository.</p>
 * <p>TODO The implementation is based on heuristics in order to make the
 * implementation easy.
 * Link validation via {@link RenderingValidator} is
 * used in order to ensure,
 * that the heuristics are good enough for now.</p>
 */
@JavaLegacy
public class LinkTranslator extends AbstractVisitor {

    private static final Pattern PROTOCOL = Pattern.compile("([a-z]+://)(.*)");
    /**
     * Matches to paths that point to a sub project's README in a git repository.
     * This helpful, when the sub projects' READMEs are embedded into the software.
     */
    private static final Pattern SUB_PROJECT_README = Pattern.compile("(\\./)?projects/[a-zA-Z\\.]+/README.md");

    private final String currentPath;
    /**
     * Number of elements inside {@link #currentPath}.
     */
    private final int currentElementCount;
    private final ProjectsRenderer projectsRenderer;

    public static LinkTranslator linkTranslator(String currentPath, ProjectsRenderer projectsRenderer) {
        return new LinkTranslator(currentPath, projectsRenderer);
    }

    private LinkTranslator(String argCurrentPath, ProjectsRenderer argProjectsRenderer) {
        currentPath = argCurrentPath;
        projectsRenderer = argProjectsRenderer;
        currentElementCount = elementCount(currentPath);
    }

    @Override
    public void visit(Image image) {
        image.setDestination(image.getDestination()
                .replace("../", "")
                .replaceAll("src\\/main\\/[a-z]+\\/", "/"));
        this.visitChildren(image);
    }

    @Override
    public void visit(Link link) {
        final var destination = link.getDestination();
        final var protocolMatch = PROTOCOL.matcher(destination);
        final String protocol;
        if (protocolMatch.matches()) {
            protocol = protocolMatch.group(1);
        } else {
            protocol = "";
        }
        final String normalizedDestination;
        final var destinationWithoutProtocol = destination
                .substring(protocol.length());
        // TODO This is an hack.
        if (SUB_PROJECT_README.matcher(destinationWithoutProtocol).matches()) {
            normalizedDestination = destinationWithoutProtocol
                    .replace("./", "")
                    .substring(8)
                    .replace(".", "/")
                    .replace("/md", ".md");
        } else {
            if (destinationWithoutProtocol.startsWith("../")) {
                final var parentCount = parentCount(destinationWithoutProtocol);
                if (parentCount < currentElementCount) {
                    final var relativePath = adjustFileSuffix(withoutSuffixElements(currentPath, parentCount)
                            + "/"
                            + withoutPrefixElements(destinationWithoutProtocol, parentCount))
                            .replace("//", "/");
                    if (projectsRenderer.projectsPaths().contains(Path.of(relativePath))) {
                        link.setDestination("/" + relativePath);
                        visitChildren(link);
                        return;
                    }
                }
            }
            normalizedDestination = destinationWithoutProtocol.replace("../", "")
                    .replaceAll("^(\\./)?[a-z\\.]+/src\\/main\\/[a-z\\-]+\\/", "/")
                    .replaceAll("^(\\./)?src\\/main\\/[a-z\\-]+\\/", "/")
                    .replaceAll("projects\\/[a-z\\.]+\\/src/main/[a-z]+/", "/")
                    .replaceAll("projects\\/[a-z\\.]+\\/", "/")
                    .replace("//", "/");
        }
        link.setDestination(protocol + normalizedDestination);
        if (protocol.isEmpty()) {
            link.setDestination(adjustFileSuffix(link.getDestination()));
        }
        visitChildren(link);
    }

    /**
     * Determines the rendered path on the server given the source file path.
     *
     * @param path
     * @return
     */
    private static String adjustFileSuffix(String path) {
        // TODO This should only be done if, the link is relative and contains "src/main/md".
        if (path.endsWith(".md")) {
            return path.substring(0, path.length() - 3) + ".html";
        }
        // TODO This should only be done if, the link is relative and contains "src/main/xml".
        if (path.endsWith(".xml")) {
            return path.substring(0, path.length() - 4) + ".html";
        }
        return path;
    }
}
