/*
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.project.renderer;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.resource.FileSystemView;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.dem.utils.StringUtils;
import net.splitcells.website.Format;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.processor.BinaryMessage;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.projects.ProjectsRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.toBytes;
import static net.splitcells.website.Format.BINARY;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;
import static net.splitcells.website.server.project.renderer.ObjectsMediaRendererI.objectsMediaRenderer;

public class ObjectsMediaRendererTest {
    @UnitTest
    public void test() {
        final var testSubject = objectsMediaRenderer(Path.of(""));
        testSubject.withMediaObject(new DiscoverableMediaRenderer() {

            @Override public ListView<String> path() {
                return list("folder", "file");
            }

            @Override public Optional<BinaryMessage> render(ProjectRenderer projectRenderer, Config config) {
                return Optional.of(binaryMessage(toBytes("test-content"), BINARY));
            }
        });
        requireEquals(testSubject.render("/folder/file", Config.create(), new ProjectRenderer() {
            @Override public FileSystemView projectFileSystem() {
                throw notImplementedYet();
            }

            @Override public Optional<byte[]> renderString(String arg) {
                return Optional.empty();
            }

            @Override public Optional<byte[]> renderHtmlBodyContent(String bodyContent, Optional<String> title, Optional<String> path, Config config, ProjectsRenderer projectsRenderer) {
                return Optional.empty();
            }

            @Override public Optional<byte[]> renderXml(String xml, LayoutConfig layoutConfig, Config config) {
                return Optional.empty();
            }

            @Override public Optional<byte[]> renderRawXml(String xml, Config config) {
                return Optional.empty();
            }

            @Override public Set<Path> projectPaths() {
                return setOfUniques();
            }

            @Override public Set<Path> relevantProjectPaths() {
                return setOfUniques();
            }

            @Override public Optional<BinaryMessage> render(String path) {
                return Optional.empty();
            }

            @Override public String resourceRootPath() {
                return "";
            }
        }).orElseThrow().getContent(), toBytes("test-content"));
    }
}
