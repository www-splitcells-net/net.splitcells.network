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

import net.splitcells.dem.Dem;

/**
 * <p>Every implementation has to have a public default constructor and needs to be thread safe.</p>
 * <p>Implementations of option values should have factory methods starting with an underscore,
 * in order to discourage their usage.
 * Access to dependencies should be done via {@link Option} and
 * {@link Dem#config()} instead of directly accessing their implementations,
 * as otherwise swapping the dependencies implementation becomes a lot harder.</p>
 * <p>
 * TODO Check if every option has public constructor without arguments.
 * <p>
 * TODO Generate dedicated Option documentation for website.
 */
public interface Option<T extends Object> {
    T defaultValue();
}
