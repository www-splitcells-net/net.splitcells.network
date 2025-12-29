/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.ui.editor.geal.example;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.gel.editor.GelEditorFileSystem;
import net.splitcells.website.Format;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.projects.ProjectsRendererI;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.gel.ui.editor.geal.EditorProcessor.PROBLEM_DEFINITION;
import static net.splitcells.website.Format.COMMON_MARK;
import static net.splitcells.website.server.messages.FieldUpdate.fieldUpdate;
import static net.splitcells.website.server.messages.FormUpdate.formUpdate;
import static net.splitcells.website.server.messages.RenderingType.PLAIN_TEXT;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

public class SportCourseExample implements ProjectsRendererExtension {
    private static final String PATH = "/net/splitcells/gel/ui/editor/geal/example/sport-course.json";

    public static SportCourseExample sportCourseExample() {
        return new SportCourseExample();
    }

    private SportCourseExample() {

    }

    @Override
    public Optional<BinaryMessage> renderFile(String path, ProjectsRendererI projectsRendererI, Config config) {
        if (PATH.equals(path)) {
            val formUpdate = formUpdate();
            val problemDefinition = fieldUpdate()
                    .setRenderingType(Optional.of(PLAIN_TEXT))
                    .setData(StringUtils.toBytes(configValue(GelEditorFileSystem.class)
                            .readString("src/main/resources/html/net/splitcells/gel/editor/geal/examples/sports-course-planning.txt")))
                    .setType(COMMON_MARK);
            formUpdate.getFields().put(PROBLEM_DEFINITION, problemDefinition);
            return Optional.of(binaryMessage(StringUtils.toBytes(formUpdate.toTree().toJsonString()), Format.TEXT_PLAIN));
        }
        return Optional.empty();
    }

    @Override public boolean requiresAuthentication(RenderRequest request) {
        return false;
    }

    @Override public Set<Path> projectPaths(ProjectsRendererI projectsRendererI) {
        return setOfUniques(Path.of(PATH.substring(1)));
    }
}
