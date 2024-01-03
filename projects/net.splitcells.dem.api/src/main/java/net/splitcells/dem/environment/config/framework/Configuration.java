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
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.utils.ExecutionException.executionException;

/**
 * TODO Split up into generic dynamically typed and type safe table and this configuration class.
 */
public interface Configuration extends ConfigurationV {

    @ReturnsThis
    default <T> Configuration withInitedOption(Class<? extends Option<T>> key) {
        try {
            return withConfigValue(key, key.getDeclaredConstructor().newInstance().defaultValue());
        } catch (Throwable e) {
            throw executionException(perspective("Could not initialize config with default value.")
                            .withProperty("key", key.getName())
                    , e);
        }
    }

    @ReturnsThis
    <T> Configuration withConfigValue(Class<? extends Option<T>> key, T value);

    /**
     * Process a certain type of resource values.
     *
     * @param type      Type of resource values, that will be processed.
     * @param processor Function that replaces current resource values.
     * @param <T>       Type of resource values, that will be processed.
     */
    <T> void process(Class<? extends T> type, Function<T, T> processor);
}
