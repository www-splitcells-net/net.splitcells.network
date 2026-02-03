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
package net.splitcells.network.worker.via.java.repo;

import net.splitcells.dem.lang.annotations.JavaLegacy;
import org.eclipse.jgit.api.Git;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;

@JavaLegacy
public class Repositories implements Repository {
    public static Repository repository(Path path) {
        return new Repositories(path);
    }

    private final Path path;
    private final Git gitIntegration;

    private Repositories(Path pathArg) {
        path = pathArg;
        try {
            gitIntegration = Git.open(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Repository commitAll() {
        try {
            final int numberOfFilesChanged = gitIntegration.add()
                    .addFilepattern("src/")
                    .setUpdate(false)
                    .call()
                    .getEntryCount();
            if (numberOfFilesChanged > 0) {
                gitIntegration.commit()
                        .setMessage("Committed by \""
                                + System.getProperty("user.name")
                                + "\" via the Network Worker on \""
                                + InetAddress.getLocalHost().getHostName()
                                + "\".")
                        .call();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}
