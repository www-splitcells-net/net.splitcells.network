package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public interface ProjectsRenderer {
    void build();

    /**
     * TODO Create flag in order to trigger incremental,
     * where only changed files are build.
     * Find out changed files via modification time or git history.
     * <p>
     * TODO This belongs to a dedicated class.
     *
     * @param target Folder where the rendered files are written to.
     */
    @Deprecated
    void serveTo(Path target);

    /**
     * TODO This belongs to a dedicated class.
     */
    @Deprecated
    void serveToHttpAt();

    /**
     * TODO This belongs to a dedicated class.
     */
    @Deprecated
    void serveAsAuthenticatedHttpsAt();

    /**
     * Renders the file of the given {@param path}.
     * This file has to be present in {@link #projectsPaths}.
     *
     * @param path This is the path of the file being rendered.
     * @return The rendered file.
     */
    Optional<RenderingResult> render(String path);

    Set<Path> projectsPaths();

    Set<Path> relevantProjectsPaths();

    Config config();

    List<ProjectRenderer> projectRenderers();

    @Deprecated
    Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config);

    /**
     * Renders a given String to the main output format.
     *
     * @param content     This is the String to be rendered.
     * @param metaContent Additional information about {@param content}.
     * @return This is the rendered String.
     */
    Optional<RenderingResult> renderContent(String content, LayoutConfig metaContent);
}
