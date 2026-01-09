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
import net.splitcells.dem.environment.config.ProgramRepresentative;
import net.splitcells.dem.environment.config.framework.Option;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.object.Discoverable;

import java.util.function.Consumer;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.StreamUtils.stream;
import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

/**
 * <p>This interface provides a single point of entry for a Java module based on {@link Dem}.
 * It bundles all settings of a module at one place.</p>
 * <p>An instance of this interface describes the configuration of a program,
 * that can be executed via {@link Dem#serve(Class[])}.
 * Such a program provides its functionality via {@link Service}.
 * Therefore, {@link Cell} can also be viewed as a {@link Service} orchestration/configuration API.
 * Another goal of this API is to bundle all configurations of a Java module,
 * at one place and relying more on convention instead of configuration.</p>
 * <p>Classes implementing this method, are required to provide a public constructor without arguments.</p>
 * <p>New properties should not be added to this interface directly,
 * but should be added via the `Cell#accept` dynamically.
 * For one, we want to avoid bloating the Cell interface.
 * Furthermore,
 * a mechanism is required whereby value of some {@link Cell} properties is replaceable by the caller.
 * For instance,
 * a filesystem associated with a {@link Cell} is a file system view based on class resources by default in most cases.
 * When a developer wants to edit the data of the read only filesystem,
 * it would be efficient,
 * if the developer could edit the source files and test the edit without an application restart in the IDE.
 * Replacing the file system view based on class resources of a {@link Cell} with a file system view backed
 * by the source code, would make this possible.
 * In effect, properties associated with a {@link Cell} should be configured via dependency injection of {@link Dem},
 * when possible.</p>
 * <p>The word Module was not used, because java imports {@link java.lang.Module} by default,
 * which causes uncomfortableness in the IDE, when trying to adjust the default import.
 * The word unit was not used, in order to avoid confusion regarding unit tests.
 * The word cell was used, as it is also used in the project name ;)</p>
 * <p>TODO Provide double book accounting for all {@link Cell} based configuration and every aspect of it.</p>
 * <p>TODO Consider testing distro configurations, in order to easy complex migrations in the future.</p>
 */
public interface Cell extends Consumer<Environment>, Discoverable, Option<Consumer<Environment>>, Runnable {
    default List<String> path() {
        return stream(getClass().getName().split("\\.")).collect(toList());
    }

    default String programName() {
        return simplifiedName(getClass());
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

    /**
     * This method is declared here, in order to improve the autocompletion of the parameter name by IDEs.
     *
     * @param env The configuration that bootstraps this {@link Cell}.
     */
    @Override
    default void accept(Environment env) {

    }

    @Override
    default Consumer<Environment> defaultValue() {
        return this;
    }

    /**
     * TODO This is an hack.
     * This method solely exist, so that {@link ProgramRepresentative} is correctly interpreted,
     * during {@link Dem#serve(Class[])} by providing a {@link Runnable}.
     * Maybe this is ok, as this provides a way to define a default {@link Cell} task after startup.
     * Maybe it is not okay.
     * {@link Dem#waitIndefinitely()} is used by default, as this is required for {@link Cell}s,
     * that represent services.
     */
    @Override
    default void run() {
        Dem.waitIndefinitely();
    }
}
