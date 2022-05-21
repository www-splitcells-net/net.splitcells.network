package net.splitcells.website.server.project.validator;

import net.splitcells.dem.environment.config.ProgramName;
import net.splitcells.dem.resource.communication.interaction.LogLevel;
import net.splitcells.dem.resource.host.HostName;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.network.worker.Logger;
import net.splitcells.website.Formats;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.project.RenderingResult;


import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

import static net.splitcells.dem.Dem.config;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.Paths.userHome;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.network.worker.Logger.logger;

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

    private int invalidLinkCount = 0;
    private String reportName = "default";

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
                            ++invalidLinkCount;
                            domsole().append("Invalid Link: " + link + ", " + resolvedLink, LogLevel.ERROR);
                        }
                        return !isValid;
                    }
                    return true;
                }).collect(toList());
        return invalid.isEmpty();
    }

    public void startReport(String name) {
        reportName = name;
        invalidLinkCount = 0;
    }

    @Override
    public void endReport() {
        final var logger = logger(userHome("Documents/projects/net.splitcells.martins.avots.support.system/public/net.splitcells.network.log"));
        logger.logExecutionResults(reportPath(reportName)
                , config().configValue(HostName.class)
                , LocalDate.now()
                , "Invalid Link Count"
                , invalidLinkCount);
        logger.commit();
    }
    
    public static String reportPath(String reportName) {
        return RenderingValidatorForHtmlLinks.class.getName().replace('.', '/') + "/" + reportName;
    }

    public int invalidLinkCount() {
        return invalidLinkCount;
    }
}
