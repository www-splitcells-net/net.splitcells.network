/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
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
}
