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

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.projects.ProjectsRenderer;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Optional;

import static net.splitcells.dem.resource.Trail.withoutSuffixElements;
import static net.splitcells.website.server.project.renderer.extension.commonmark.LinkTranslator.linkTranslator;

public class CommonMarkIntegration {

    /**
     * This string is split into multiple lines,
     * in order to prevent REUSE compliance checkers to falsely classify this source code part as real license info.
     */
    private static final String LICENSE_HEADER = "----\n"
            + "* SPDX-License-Identifier"
            + ": EPL-2.0 OR GPL-2.0-or-later\n"
            + "* SPDX-FileCopyrightText"
            + ": Contributors To The `net.splitcells.*` Projects\n"
            + "----\n";

    public static CommonMarkIntegration commonMarkIntegration() {
        return new CommonMarkIntegration();
    }

    final Parser parser = Parser.builder().build();
    final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private CommonMarkIntegration() {
    }

    public byte[] render(String arg, ProjectRenderer projectRenderer, String path, Config config
            , ProjectsRenderer projectsRenderer) {
        final Optional<String> title;
        final String contentToRender;
        if (arg.startsWith(LICENSE_HEADER)) {
            arg = arg.substring(LICENSE_HEADER.length()) + "\n\n" + LICENSE_HEADER;
        }
        if (arg.startsWith("#")) {
            final var titleLine = arg.split("[\r\n]+")[0];
            /** This is a dirty quick hack. Note that rendering the title via CommonMark library,
             * would not fix the issue, as element symbols are escaped during title rendering via
             * {@link projectRenderer}.
             */
            title = Optional.of(titleLine.replace("#", "")
                    .replaceAll("\\[™\\]\\(.*\\)", "™")
                    .trim());
            contentToRender = arg.substring(titleLine.length());
        } else {
            title = Optional.empty();
            contentToRender = arg;
        }
        final var parsed = parser.parse(contentToRender);
        parsed.accept(linkTranslator(withoutSuffixElements(path, 1), projectsRenderer));
        return projectRenderer
                .renderHtmlBodyContent(renderer.render(parsed)
                        , title
                        , Optional.of(path)
                        , config
                        , projectsRenderer)
                .orElseThrow();
    }

    public String render(Node node) {
        return renderer.render(node);
    }

    public List<Event> events(String eventsAsCommonMark, ProjectRenderer projectRenderer, String path, Config config) {
        final Optional<String> title;
        final String contentToRender;
        if (eventsAsCommonMark.startsWith("#")) {
            final var titleLine = eventsAsCommonMark.split("[\r\n]+")[0];
            title = Optional.of(titleLine.replace("#", "").trim());
            contentToRender = eventsAsCommonMark.substring(titleLine.length());
        } else {
            title = Optional.empty();
            contentToRender = eventsAsCommonMark;
        }
        final var parsed = parser.parse(contentToRender);
        final var eventExtractor = EventExtractor.eventExtractor();
        parsed.accept(eventExtractor);
        return eventExtractor.extractedEvents();
    }
}
