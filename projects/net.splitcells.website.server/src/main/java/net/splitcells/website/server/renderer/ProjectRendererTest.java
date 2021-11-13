package net.splitcells.website.server.renderer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.website.server.renderer.ProjectRenderer.projectRenderer;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectRendererTest {
    @Test
    public void testCommonMarkLayout() {
        final var testSubject = projectRenderer
                ("test"
                        , Path.of("../net.splitcells.dem")
                        , Path.of("../net.splitcells.website.default.content/src/main/xsl/net/splitcells/website/den/translation/to/html/")
                        , Path.of("net.splitcells.website.default.content/src/main/resources/html")
                        , "/net/splitcells/dem"
                        , a -> Optional.empty());
        assertThat(testSubject.projectPaths()).contains(Path.of("net/splitcells/dem/guidelines/technology-stack.md"));
    }
}
