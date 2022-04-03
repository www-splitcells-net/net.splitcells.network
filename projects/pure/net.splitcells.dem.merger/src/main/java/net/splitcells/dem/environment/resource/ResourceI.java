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

/**
 * TODO Instead of an abstract class, create multiple {@link Resource} sub
 * interfaces, so that inheritance is not required by default,
 * in order to implement {@link net.splitcells.dem.environment.config.framework.Option}s
 * and {@link Resource}s easily.
 * 
 * @param <T> Type Of Resource
 */
public class ResourceI<T extends Closeable & Flushable> extends OptionI<T> implements Resource<T> {
    public ResourceI(Supplier<T> arg_default_value) {
        super(arg_default_value);
    }
}
