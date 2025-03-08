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
package net.splitcells.dem.data.set.map;

import net.splitcells.dem.environment.resource.Resource;
import net.splitcells.dem.lang.annotations.JavaLegacyBody;

public interface MapFactory extends Resource {

    <K, V> Map<K, V> map();

    @JavaLegacyBody
    <K, V> Map<K, V> map(java.util.Map<K, V> arg);

    /**
     * Usually nothing needs to be done.
     */
    default void flush() {

    }
}
