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
package net.splitcells.website.server.renderer.renderer.commonmark;

import net.splitcells.website.server.renderer.ProjectRenderer;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Optional;

import static net.splitcells.website.server.renderer.renderer.commonmark.LinkTranslator.linkTranslator;

public class CommonMarkRenderer {
    public static CommonMarkRenderer commonMarkRenderer() {
        return new CommonMarkRenderer();
    }

    final Parser parser = Parser.builder().build();
    final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private CommonMarkRenderer() {
    }

    public byte[] render(String arg, ProjectRenderer projectRenderer, String path) {
        final Optional<String> title;
        final String contentToRender;
        if (arg.startsWith("#")) {
            final var titleLine = arg.split("[\r\n]+")[0];
            title = Optional.of(titleLine.replaceAll("#", "").trim());
            contentToRender = arg.substring(titleLine.length());
        } else {
            title = Optional.empty();
            contentToRender = arg;
        }
        final var parsed = parser.parse(contentToRender);
        parsed.accept(linkTranslator());
        return projectRenderer
                .renderHtmlBodyContent(renderer.render(parsed)
                        , title
                        , Optional.of(path))
                .get();
    }
}
