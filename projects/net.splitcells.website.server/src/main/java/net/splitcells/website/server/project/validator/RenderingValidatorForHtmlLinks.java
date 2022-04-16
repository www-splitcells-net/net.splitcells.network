package net.splitcells.website.server.project.validator;

import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.website.Formats;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.project.RenderingResult;


import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.data.set.list.Lists.toList;

/**
 * Checks whether rendered HTML documents relative links can be rendered
 * by the given {@link ProjectsRendererI}.
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
    public boolean validate(Optional<RenderingResult> content, ProjectsRendererI projectsRendererI, Path requestedPath) {
        if (content.isEmpty()) {
            return true;
        }
        if (!Formats.HTML.mimeTypes().equals(content.get().getFormat())) {
            return true;
        }
        final var paths = projectsRendererI.projectsPaths();
        final var invalid = CommonFunctions.selectMatchesByRegex(
                        CommonFunctions.bytesToString(content.get().getContent())
                        , HTML_HREF
                        , 2)
                .filter(link -> !link.startsWith("http://") && !link.startsWith("https://"))
                .filter(link -> {
                    /**
                     * TODO Move path checking to dedicated method at {@link ProjectsRendererI}.
                     */
                    final var resolvedLinkString = requestedPath
                            .getParent()
                            .resolve(Path.of(link.replace("//", "/")))
                            .normalize()
                            .toString();
                    final Path resolvedLink;
                    if (resolvedLinkString.startsWith("/")) {
                        resolvedLink = Path.of(resolvedLinkString.substring(1));
                    } else {
                        resolvedLink = Path.of(resolvedLinkString);
                    }
                    /**
                     * If this is not done, links containing Javascript or
                     * consisting only of id,
                     * would be checked as well.
                     */
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
