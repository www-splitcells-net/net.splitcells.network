/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server;

import java.util.Optional;

/**
 * Defines a program, that is usable on the webserver and accessible by a path.
 */
public class ProgramConfig {
    public static ProgramConfig programConfig(String name, String path) {
        return new ProgramConfig(name, path);
    }

    private final String name;
    private final String path;
    private Optional<String> description = Optional.empty();

    private Optional<String> logoPath = Optional.empty();
    private boolean isPathUrl = false;

    private ProgramConfig(String nameArg, String pathArg) {
        name = nameArg;
        path = pathArg;
    }

    public String name() {
        return name;
    }

    public String path() {
        return path;
    }

    public Optional<String> description() {
        return description;
    }

    public ProgramConfig withDescription(Optional<String> arg) {
        description = arg;
        return this;
    }

    public Optional<String> logoPath() {
        return logoPath;
    }

    public ProgramConfig withLogoPath(Optional<String> arg) {
        logoPath = arg;
        return this;
    }

    public boolean isPathUrl() {
        return isPathUrl;
    }

    public ProgramConfig withIsPathUrl(boolean arg) {
        isPathUrl = arg;
        return this;
    }
}
