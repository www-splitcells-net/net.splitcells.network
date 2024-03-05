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
package net.splitcells.dem.environment;

import net.splitcells.dem.Dem;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.resource.FileSystemView;

import java.util.function.Consumer;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.resource.FileSystemViaClassResources.fileSystemViaClassResources;
import static net.splitcells.dem.utils.StreamUtils.stream;

/**
 * <p>This interface provides a single point of entry for a Java module based on {@link Dem}.</p>
 * <p>An instance of this interface describes the configuration of a program,
 * that can be executed via {@link Dem#serve(Class[])}.
 * Another goal of this API is to bundle all configurations of a Java module,
 * at one place and relying more on convention instead of configuration.</p>
 * <p>Classes implementing this method, are required to provide a public constructor without arguments.</p>
 * <p>The word Module was not used, because java imports {@link java.lang.Module} by default,
 * which causes uncomfortableness, when trying to adjust the default import.
 * The word unit was not used, in order to avoid confusion regarding unit tests.
 * The word cell was used, as it is also used in the project name ;)</p>
 */
public interface Cell extends Consumer<Environment>, Discoverable, Option<Consumer<Environment>> {
    default List<String> path() {
        return stream(getClass().getName().split("\\.")).collect(toList());
    }

    /**
     * @return The return value corresponds to the groupId of the Maven project,
     * that contains this interface implementation.
     */
    String groupId();

    /**
     * @return The return value in general corresponds to the artifactId of the Maven project,
     * that contains this interface implementation.
     */
    String artifactId();

    default FileSystemView fileSystemView() {
        return fileSystemViaClassResources(Dem.class, groupId(), artifactId());
    }

    /**
     * This method is declared here, in order to improve the autocompletion of the parameter name by IDEs.
     *
     * @param env The configuration that bootstraps this {@link Cell}.
     */
    @Override
    void accept(Environment env);

    @Override
    default Consumer<Environment> defaultValue() {
        return this::accept;
    }
}
