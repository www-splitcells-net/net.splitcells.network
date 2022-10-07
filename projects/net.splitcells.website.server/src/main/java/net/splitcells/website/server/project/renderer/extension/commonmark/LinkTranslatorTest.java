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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.commonmark.node.Link;
import org.junit.jupiter.api.Test;

import static net.splitcells.website.server.project.renderer.extension.commonmark.LinkTranslator.linkTranslator;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JavaLegacyArtifact
public class LinkTranslatorTest {
    @Test
    public void testSourceRelativeLink() {
        final var testSubject = linkTranslator();
        final var testData = new Link("./src/main/md/net/splitcells/network/guidelines/changelog.md", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/network/guidelines/changelog.html");
    }

    @Test
    public void testSourceRelativeParentLink() {
        final var testSubject = linkTranslator();
        final var testData = new Link("../../../../../../src/main/xml/net/splitcells/gel/objectives.xml", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/gel/objectives.html");
    }

    @Test
    public void testSourceProjectParentRelativeLink() {
        final var testSubject = linkTranslator();
        final var testData = new Link("../../../../../../../../projects/net.splitcells.gel.sheath/src/main/md/net/splitcells/gel/test/functionality/n-queen-problem.md", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/gel/test/functionality/n-queen-problem.html");
    }

    @Test
    public void testSourceProjectRelativeLink() {
        final var testSubject = linkTranslator();
        final var testData = new Link("src/main/xml-pom/net/splitcells/network/build-with-github-snapshot.pom.xml", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/network/build-with-github-snapshot.pom.html");
    }
}
