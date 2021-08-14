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
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.framework.OptionI;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.function.Supplier;

public abstract class ResourceI<T extends Closeable & Flushable> extends OptionI<T> implements Resource<T> {
    public ResourceI(Supplier<T> arg_default_value) {
        super(arg_default_value);
    }
}
