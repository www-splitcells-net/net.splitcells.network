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
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.framework.OptionI;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

import java.util.function.Supplier;

/**
 * @param <T> Type Of Resource
 * @deprecated Use {@link ResourceOption} interface directly instead.
 */
public class ResourceOptionI<T extends Resource> extends OptionI<T> implements ResourceOption<T> {
    public ResourceOptionI(Supplier<T> arg_default_value) {
        super(arg_default_value);
    }
}
