package net.splitcells.website.server.project.renderer.commonmark;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.testing.TestTypes.INTEGRATION_TEST;
import static net.splitcells.website.server.project.ProjectRenderer.projectRenderer;
import static org.assertj.core.api.Assertions.assertThat;

public class CommonMarkRendererTest {
    /**
     * TODO Test extension explicitly.
     */
    @Tag(INTEGRATION_TEST)
    @Test
    public void testCommonMarkLayout() {
        final var testSubject = projectRenderer
                ("public"
                        , Path.of("../net.splitcells.dem")
                        , Path.of("../net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                        , Path.of("net.splitcells.website.default.content/src/main/resources/html")
                        , "/net/splitcells/dem"
                        , a -> Optional.empty());
        assertThat(testSubject.projectPaths()).contains(Path.of("net/splitcells/dem/guidelines/technology-stack.html"));
    }
}
