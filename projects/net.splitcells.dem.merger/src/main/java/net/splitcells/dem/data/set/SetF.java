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
package net.splitcells.dem.data.set;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

/**
 * TODO Specify expected min/average/max size
 * <p>
 * TODO Specify required performance signature.
 */
public interface SetF extends Resource {

    <T> Set<T> set();

    @JavaLegacyBody
    <T> Set<T> set(java.util.Collection<T> arg);

    @Deprecated
    @JavaLegacyBody
    <T> java.util.Set<T> lagacySet();

    @Deprecated
    @JavaLegacyBody
    <T> java.util.Set<T> legacySet(java.util.Collection<T> arg);

    /**
     * Most implementations won't have to do anything.
     */
    @Override
    default void close() {
        return;
    }

    /**
     * Most implementations won't have to do anything.
     */
    @Override
    default void flush() {
        return;
    }
}
