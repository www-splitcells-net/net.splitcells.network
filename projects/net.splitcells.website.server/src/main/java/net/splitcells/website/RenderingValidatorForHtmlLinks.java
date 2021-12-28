package net.splitcells.website;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.website.server.project.ProjectsRenderer;
import net.splitcells.website.server.project.RenderingResult;


import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * Checks whether rendered HTML documents relative links can be rendered
 * by the given {@link ProjectsRenderer}.
 * Validation is done via Regex heuristics,
 * in order to avoid additional external dependencies.
 */
public class RenderingValidatorForHtmlLinks implements RenderingValidator {
    private static Pattern HTML_HREF = Pattern.compile("(href=\\\")([^\\\"]*)(\\\")");
    private static Pattern PATH = Pattern.compile("(\\.)?(\\/)?([a-zA-Z0-9\\.\\-]+\\/)*[a-zA-Z0-9\\.\\-]+");

    public static RenderingValidatorForHtmlLinks renderingValidatorForHtmlLinks() {
        return new RenderingValidatorForHtmlLinks();
    }

    private RenderingValidatorForHtmlLinks() {
    }

    @Override
    public boolean validate(Optional<RenderingResult> content, ProjectsRenderer projectsRenderer, Path requestedPath) {
        if (content.isEmpty()) {
            return true;
        }
        if (!Formats.HTML.mimeTypes().equals(content.get().getFormat())) {
            return true;
        }
        final var paths = projectsRenderer.projectsPaths();
        final var invalid = CommonFunctions.selectMatchesByRegex(
                        CommonFunctions.bytesToString(content.get().getContent())
                        , HTML_HREF
                        , 2)
                .filter(link -> !link.startsWith("http://") && !link.startsWith("https://"))
                .filter(link -> {
                    /**
                     * TODO Move path checking to dedicated method at {@link ProjectsRenderer}.
                     */
                    final var resolvedLinkString = requestedPath.resolve(Path.of(link.replace("//", "/")))
                            .toString();
                    final Path resolvedLink;
                    if (resolvedLinkString.startsWith("/")) {
                        resolvedLink = Path.of(resolvedLinkString.substring(1));
                    } else {
                        resolvedLink = Path.of(resolvedLinkString);
                    }
                    if (PATH.matcher(resolvedLink.toString()).matches()) {
                        final var isValid = paths.contains(resolvedLink);
                        // TODO HACK
                        if (!isValid) {
                            System.out.println("Invalid Link: " + link + ", " + resolvedLink);
                        }
                        return !isValid;
                    }
                    return true;
                }).collect(toList());
        return invalid.isEmpty();
    }
}
