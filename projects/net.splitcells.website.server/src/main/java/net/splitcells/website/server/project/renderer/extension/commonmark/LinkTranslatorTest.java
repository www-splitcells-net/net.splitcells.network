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
import net.splitcells.website.server.Config;
import org.commonmark.node.Link;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.website.server.project.renderer.extension.commonmark.LinkTranslator.linkTranslator;
import static net.splitcells.website.server.projects.ProjectsRendererI.projectsRenderer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JavaLegacy
public class LinkTranslatorTest {
    @Test
    public void testSourceRelativeLink() {
        final var testSubject = linkTranslator("", projectsRenderer("test", a -> null, a -> list(), Config.create()));
        final var testData = new Link("./src/main/md/net/splitcells/network/guidelines/changelog.md", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/network/guidelines/changelog.html");
    }

    @Test
    public void testSourceRelativeParentLink() {
        final var testSubject = linkTranslator("", projectsRenderer("test", a -> null, a -> list(), Config.create()));
        final var testData = new Link("../../../../../../src/main/xml/net/splitcells/gel/objectives.xml", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/gel/objectives.html");
    }

    @Test
    public void testSourceProjectParentRelativeLink() {
        final var testSubject = linkTranslator("", projectsRenderer("test", a -> null, a -> list(), Config.create()));
        final var testData = new Link("../../../../../../../../projects/net.splitcells.gel.sheath/src/main/md/net/splitcells/gel/test/functionality/n-queen-problem.md", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/gel/test/functionality/n-queen-problem.html");
    }

    @Test
    public void testSourceProjectRelativeLink() {
        final var testSubject = linkTranslator("", projectsRenderer("test", a -> null, a -> list(), Config.create()));
        final var testData = new Link("src/main/xml-pom/net/splitcells/network/build-with-github-snapshot.pom.xml", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/network/build-with-github-snapshot.pom.html");
    }

    @Test
    public void testSourceProjectCrossReposLink() {
        final var testSubject = linkTranslator("", projectsRenderer("test", a -> null, a -> list(), Config.create()));
        final var testData = new Link("../../../../../../../../../../net.splitcells.network.community/src/main/md/net/splitcells/network/community/blog/articles/2025-04-29-consolidating-the-projects-focus.md", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/network/community/blog/articles/2025-04-29-consolidating-the-projects-focus.html");
    }
}
