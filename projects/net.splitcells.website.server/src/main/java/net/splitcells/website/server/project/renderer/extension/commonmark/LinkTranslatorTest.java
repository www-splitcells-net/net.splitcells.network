package net.splitcells.website.server.project.renderer.extension.commonmark;

import org.commonmark.node.Link;
import org.junit.jupiter.api.Test;

import static net.splitcells.website.server.project.renderer.extension.commonmark.LinkTranslator.linkTranslator;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    public void testSourceProjectRelativeLink() {
        final var testSubject = linkTranslator();
        final var testData = new Link("../../../../../../../../projects/net.splitcells.gel.sheath/src/main/md/net/splitcells/gel/test/functionality/n-queen-problem.md", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/gel/test/functionality/n-queen-problem.html");
    }


}
