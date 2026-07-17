/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.network.system;

import net.splitcells.dem.Dem;
import net.splitcells.dem.environment.Cell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.config.StartServicesAutomatically;
import net.splitcells.dem.resource.Trail;
import net.splitcells.dem.testing.annotations.IntegrationTest;
import net.splitcells.dem.testing.annotations.UnitTest;
import net.splitcells.website.server.ServerConfig;
import net.splitcells.website.server.projects.ProjectsRendererOption;
import net.splitcells.website.server.projects.RenderRequest;
import net.splitcells.website.server.security.authentication.Authentication;
import org.junit.jupiter.api.Disabled;

import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.Dem.*;
import static net.splitcells.website.server.security.authentication.Authentication.anonymous;
import static net.splitcells.website.server.security.authentication.UserSession.ANONYMOUS_USER_SESSION;

public class SystemCellTest {

    @IntegrationTest public void testServingWebsiteToFolder() {
        process(() -> Dem.configValue(ProjectsRendererOption.class).orElseThrow().serveTo(Paths.get("target/test"))
                , env -> env.config().withConfigValue(StartServicesAutomatically.class, false)
                , SystemCell.class
        );
    }

    @UnitTest public void testInvalidPath() {
        process(() -> Dem.configValue(ProjectsRendererOption.class).orElseThrow().render(RenderRequest.renderRequest(
                        Trail.trail("invalid-path")
                        , Optional.empty()
                        , anonymous()))
                , env -> env.config().withConfigValue(StartServicesAutomatically.class, false)
                , SystemCell.class);
    }

    @UnitTest public void testConfig() {
        testSerializeConfiguration(SystemCell.class, """
                * Configuration Serialization:
                    * net.splitcells.cin.CinFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.cin.CinFileSystem
                    * net.splitcells.dem.DemApiFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.dem.Dem
                    * net.splitcells.dem.DemCell: net.splitcells.dem.DemCell
                    * net.splitcells.dem.DemFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.dem.DemFileSystem
                    * net.splitcells.dem.environment.config.Initialized: true
                    * net.splitcells.dem.environment.config.IsDeterministic: Optional.empty
                    * net.splitcells.dem.environment.config.ProgramLocalIdentity: class net.splitcells.network.system.SystemCell
                    * net.splitcells.dem.environment.config.ProgramName: net.splitcells.network.system.SystemCell
                    * net.splitcells.dem.environment.config.ProgramRepresentative: net.splitcells.network.system.SystemCell
                    * net.splitcells.dem.environment.config.StartServicesAutomatically: false
                    * net.splitcells.dem.environment.config.StartTime: 2026-05-20T22:35:10Z[UTC]
                    * net.splitcells.dem.resource.communication.log.IsEchoToFile: false
                    * net.splitcells.dem.resource.host.ProcessPath: This value is host specific.
                    * net.splitcells.gel.GelCoreFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.gel.GelCoreFileSystem
                    * net.splitcells.gel.doc.GelDocFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.gel.doc.GelDocFileSystem
                    * net.splitcells.gel.editor.GelEditorFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.gel.editor.GelEditorFileSystem
                    * net.splitcells.gel.ext.GelExtFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.gel.ext.GelExtFileSystem
                    * net.splitcells.gel.ui.GelUiCell: net.splitcells.gel.ui.GelUiCell
                    * net.splitcells.gel.ui.GelUiFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.gel.ui.GelUiFileSystem
                    * net.splitcells.network.NetworkFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.network.NetworkFileSystem
                    * net.splitcells.network.system.SystemCell: net.splitcells.network.system.SystemCell
                    * net.splitcells.network.system.SystemsFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.network.system.SystemsFileSystem
                    * net.splitcells.network.worker.via.java.NetworkWorkerFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.network.worker.via.java.NetworkWorkerFileSystem
                    * net.splitcells.project.ProjectFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.project.ProjectFileSystem
                    * net.splitcells.shell.OsiFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.shell.OsiFileSystem
                    * net.splitcells.shell.lib.OsiLibFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.shell.lib.OsiLibFileSystem
                    * net.splitcells.website.WebsiteServerFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.website.WebsiteServerFileSystem
                    * net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem: net.splitcells.dem.resource.FileSystemViaClassResourcesImpl based on net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem
                    * net.splitcells.website.server.ServerConfig:
                        * net.splitcells.website.server.Config:
                            * isSecured: false
                            * sslKeystorePassword: Optional.empty
                            * sslKeystoreFile: Optional.empty
                            * xmlSchema: Optional[src/main/xsd/den.xsd]
                            * mainProjectRepositoryPath: Optional.empty
                            * openPort: 8443
                            * generationStyle: standard
                            * cssFiles: [net/splitcells/website/css/tabulator.min.css, net/splitcells/website/css/dragula.min.css, net/splitcells/website/css/highlight.css, net/splitcells/website/css/theme.white.variables.css, net/splitcells/website/css/basic.themed.css, net/splitcells/website/css/basic.css, net/splitcells/website/css/den.css, net/splitcells/website/css/layout.default.css, net/splitcells/website/css/theme.css]
                            * jsBackgroundFiles: [net/splitcells/website/js/dragula.min.js, net/splitcells/website/js/codemirror-editor-bundle.js, net/splitcells/website/js/jquery.js, net/splitcells/website/js/highlight.js, net/splitcells/website/js/basic.js, net/splitcells/website/js/basic.default.js]
                            * siteFolder: null
                            * rootPath: /
                            * sitesName: splitcells.net
                            * rootIndex: /index.html
                            * possibleRootIndex: [/index.html, index.html, , /]
                            * cacheRenderers: false
                            * mutableProjectsPath: true
                            * isServerForGeneralPublic: true
                            * isDownloadingViaHtmlElement: false
                            * isRenderingStaticWebsite: false
                            * isMultiThreaded: false
                            * licensePages: [net/splitcells/network/LICENSE.html named as Licensing Info of The Core Project net.splitcells.network, net/splitcells/network/NOTICE.html named as Copyright Notice, net/splitcells/network/legal/Developer_Certificate_of_Origin.v1.1.html named as Developer Certificate of Origin Version 1.1, net/splitcells/network/legal/licenses/Apache-2.0.html named as Apache License - Version 2.0, January 2004, net/splitcells/network/legal/licenses/BSD-2-Clause.html named as BSD 2 Clause License, net/splitcells/network/legal/licenses/BSD-3-Clause.html named as BSD 3 Clause License, net/splitcells/network/legal/licenses/CC-BY-SA-4.0.html named as Attribution-ShareAlike 4.0 International License, net/splitcells/network/legal/licenses/EPL-2.0.html named as Eclipse Public License - v 2.0, net/splitcells/network/legal/licenses/GPL-2.0-or-later-WITH-Classpath-exception-2.0.html named as GNU GENERAL PUBLIC LICENSE - Version 2, June 1991 with Classpath Exception, net/splitcells/network/legal/licenses/GPL-2.0-or-later.html named as GNU GENERAL PUBLIC LICENSE - Version 2, June 1991, net/splitcells/network/legal/licenses/ISC.html named as ISC License, net/splitcells/network/legal/licenses/LGPL-2.1-or-later.html named as GNU LESSER GENERAL PUBLIC LICENSE - Version 2.1, February 1999, net/splitcells/network/legal/licenses/MIT.html named as MIT License, net/splitcells/network/legal/licenses/MPL-2.0.html named as Mozilla Public License Version 2.0]
                            * interactiveServer: https://live.splitcells.net/
                            * validateLinks: true
                    * net.splitcells.website.server.WebsiteServerCell: net.splitcells.website.server.WebsiteServerCell
                    * net.splitcells.website.server.test.HtmlLiveTest: class net.splitcells.website.server.test.HtmlLiveTest is enabled.
                """);
    }
}
