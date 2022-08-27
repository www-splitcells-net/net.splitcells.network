package net.splitcells.website.server.project.renderer.extension.commonmark;

import org.commonmark.node.Link;
import org.junit.jupiter.api.Test;

import static net.splitcells.website.server.project.renderer.extension.commonmark.LinkTranslator.linkTranslator;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LinkTranslatorTest {
    @Test
    public void test() {
        final var testSubject = linkTranslator();
        final var testData = new Link("./src/main/md/net/splitcells/network/guidelines/changelog.md", "here");
        testSubject.visit(testData);
        assertThat(testData.getDestination()).isEqualTo("/net/splitcells/network/guidelines/changelog.html");
    }
}
