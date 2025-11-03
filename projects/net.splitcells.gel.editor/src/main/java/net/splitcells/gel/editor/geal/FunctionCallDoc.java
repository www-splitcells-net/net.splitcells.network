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
package net.splitcells.gel.editor.geal;

import lombok.val;
import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.resource.HostUtilizationRecordService;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.XmlConfig;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.Trail;
import net.splitcells.gel.editor.geal.runners.FunctionCallRunnerParser;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.RenderResponse;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;
import net.splitcells.website.server.projects.extension.impls.ProjectPathsRequest;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.order.Comparators.ASCENDING_STRINGS;
import static net.splitcells.dem.data.order.Comparators.ascendingString;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.namespace.NameSpaces.SEW;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.lang.tree.XmlConfig.xmlConfig;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Trail.trail;
import static net.splitcells.gel.editor.Editor.editor;
import static net.splitcells.gel.editor.geal.runners.FunctionCallMetaExecutor.functionCallMetaExecutor;
import static net.splitcells.gel.editor.geal.runners.ParserDocumentConfig.parserDocumentConfig;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.projects.RenderResponse.renderResponse;

public class FunctionCallDoc implements ProjectsRendererExtension {

    public static FunctionCallDoc functionCallDoc() {
        return new FunctionCallDoc();
    }

    private static final Trail PATH = trail("net/splitcells/gel/editor/geal/function-call-doc.html");

    private FunctionCallDoc() {

    }

    public static List<Tree> generateDoc() {
        val editor = editor("Documentation Generation", Discoverable.EXPLICIT_NO_CONTEXT);
        val subject = functionCallMetaExecutor();
        final List<Tree> doc = list();
        subject.parsers().sorted(ascendingString(FunctionCallRunnerParser::getName)).forEach(p -> {
            doc.add(p.document(editor
                    , parserDocumentConfig().setRenderVariation(subject.hasFunctionCallNameVariations(p.getName()))));
        });
        return doc;
    }

    @Override
    public RenderResponse render(RenderRequest request, ProjectsRenderer projectsRenderer) {
        if (!request.trail().equalContents(PATH)) {
            return renderResponse();
        }
        val article = tree("article", SEW)
                .withChildren(tree("meta", SEW)
                                .withChild(tree("path", SEW).withText(request.trail().unixPathString()))
                                .withChild(tree("title", SEW).withText("Geal Function Call Documentation"))
                        , tree("content", SEW).withChildren(generateDoc())
                );
        return renderResponse(Optional.of(binaryMessage(projectsRenderer.projectRenderers().get(0)
                        .renderRawXml(article.toXmlStringWithAllNameSpaceDeclarationsAtTop(), projectsRenderer.config())
                        .orElseThrow()
                , HTML_TEXT.codeName())));
    }


    @Override public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override public Set<Path> projectPaths(ProjectsRendererI projectsRenderer) {
        return setOfUniques();
    }

    @Override
    public Set<Path> projectPaths(ProjectPathsRequest request) {
        return setOfUniques(Path.of(PATH.unixPathString()));
    }
}
