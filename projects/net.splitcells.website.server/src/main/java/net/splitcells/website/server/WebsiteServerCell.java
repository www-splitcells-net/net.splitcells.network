/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server;

import net.splitcells.dem.DemCell;
import net.splitcells.dem.environment.Environment;
import net.splitcells.dem.environment.Cell;
import net.splitcells.website.WebsiteServerFileSystem;
import net.splitcells.website.content.defaults.WebsiteContentDefaultsFileSystem;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.Dem.serve;
import static net.splitcells.website.server.ProjectConfig.projectConfig;

public class WebsiteServerCell implements Cell {
    public static void main(String... args) {
        serve(WebsiteServerCell.class);
    }

    @Override
    public String groupId() {
        return "net.splitcells";
    }

    @Override
    public String artifactId() {
        return "website.server";
    }

    /**
     * TODO In the future, the default content should be dependent on the server and not the other way around.
     *
     * @param env the input argument
     */
    @Override
    public void accept(Environment env) {
        env.withCell(DemCell.class);
        configureNoneCellInit(env.config().configValue(ServerConfig.class));
        env.config().withInitedOption(ServerService.class);
    }

    public static void configureNoneCellInit(Config conf) {
        conf
                .withAdditionalProject(projectConfig("/"
                        , configValue(WebsiteServerFileSystem.class)))
                .withAdditionalProject(projectConfig("/"
                        , configValue(WebsiteContentDefaultsFileSystem.class)))
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/jquery.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/highlight.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.js")
                .withAdditionalJsBackgroundFiles("net/splitcells/website/js/basic.default.js")
                .withAdditionalCssFile("net/splitcells/website/css/theme.white.variables.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.themed.css")
                .withAdditionalCssFile("net/splitcells/website/css/basic.css")
                .withAdditionalCssFile("net/splitcells/website/css/den.css")
                .withAdditionalCssFile("net/splitcells/website/css/layout.default.css")
                .withAdditionalCssFile("net/splitcells/website/css/theme.css")
        ;
    }
}
