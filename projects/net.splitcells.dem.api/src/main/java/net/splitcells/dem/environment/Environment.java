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
import net.splitcells.dem.environment.config.framework.ConfigDependencyRecording;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.environment.resource.Service;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.function.Consumer;

import static net.splitcells.dem.data.atom.Thing.instance;

/**
 * <p>TODO Merge dem.environment package content into dem.resources package.</p>
 * <p>TODO Create a meta config in form of a table, in order to add arbitrary information, methods etc. to a config.
 * This would allow one to omit reflection in order to close all {@link net.splitcells.dem.environment.resource.Resource}
 * during {@link #close()}, for instance.
 * This would also be a way to generate state reports or documentation via injection mechanisms.
 * Thereby, the whole environment configuration could be modelled as a table,
 * which would simplify the visualization.</p>
 */
public interface Environment extends EnvironmentV, Service {

    /**
     * It is not allowed to be called multiple times.
     */
    void init();

    Configuration config();

    /**
     * This helper method makes it easier to write and read long configuration chains.
     *
     * @param configurator
     * @return
     */
    @ReturnsThis default Environment configure(Consumer<Environment> configurator) {
        configurator.accept(this);
        return this;
    }

    /**
     *
     * @param clazz
     * @param <T>
     * @return
     * @see #withCell(Class, Consumer)
     */
    default <T extends Cell> Environment withCell(Class<T> clazz) {
        return withCell(clazz, c -> {
        });
    }

    /**
     * This method is the base in order to declaratively configure {@link Dem#process(Runnable, Consumer)}.
     *
     * @param clazz        This {@link Cell} class is instantiated and then applied as a configuration.
     * @param cellConsumer After the configuration is applied,
     *                     one can call optional configurations of the {@link Cell} in this {@link Consumer}.
     * @param <T>          This is the type of the {@link Cell} being used for configuration.
     * @return Returns this.
     */
    <T extends Cell> Environment withCell(Class<T> clazz, Consumer<T> cellConsumer);

    default Environment withConfig(Consumer<Environment> configurator) {
        configurator.accept(this);
        return this;
    }

}
