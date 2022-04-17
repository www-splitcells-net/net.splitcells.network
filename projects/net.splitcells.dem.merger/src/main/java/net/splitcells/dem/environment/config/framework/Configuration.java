/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.lang.annotations.Secret;
import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.function.Function;

/**
 * TODO Split up into generic dynamically typed and type safe table and this configuration class.
 */
public interface Configuration extends ConfigurationV {

    @ReturnsThis
    default <T> Configuration with_inited_option(Class<? extends Option<T>> key) {
        try {
            return withConfigValue(key, key.getDeclaredConstructor().newInstance().defaultValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
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
