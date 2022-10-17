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
package net.splitcells.dem.resource;

import net.splitcells.dem.environment.resource.Resource;

import java.util.function.Consumer;

/**
 * This interface is intended for {@link Resource} instances, that represent factories.
 * In this case, this interface is used, in order to process newly created objects.
 * This process is called {@link #connect},
 * because the processor may subscribe to events created by the processed subject.
 *
 * @param <T> This is the type of object being listened to.
 */
public interface ConnectingConstructor<T> {
    /**
     * This adds another object creation listener.
     *
     * @param connector This is the object creation listener.
     * @return This
     */
    ConnectingConstructor<T> withConnector(Consumer<T> connector);

    /**
     * Processes the given instances via the listeners.
     *
     * @param subject Object to be processed.
     */
    void connect(T subject);
}
