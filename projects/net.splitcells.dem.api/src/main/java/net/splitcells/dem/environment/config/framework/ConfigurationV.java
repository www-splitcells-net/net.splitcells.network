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

import net.splitcells.dem.data.set.Set;

import java.util.function.BiConsumer;

public interface ConfigurationV {
    <T> T configValue(Class<? extends Option<T>> key);

    Object configValueUntyped(Object key);

    Set<Class<? extends Option<?>>> keys();

    /**
     * Consumes a certain type of {@link Option} and {@link Option#defaultValue()}.
     *
     * @param type        Type of {@link Option}, that will be processed.
     * @param consumer    Function that consumes the corresponding {@link Option} and their values.
     * @param <V> Type of {@link Option#defaultValue()}, that will be consumed.
     */
    <K extends Class<? extends Option<V>>, V> void consume(K type, BiConsumer<K, V> consumer);
}
