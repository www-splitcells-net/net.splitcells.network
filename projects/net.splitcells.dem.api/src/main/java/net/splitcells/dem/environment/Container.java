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
package net.splitcells.dem.environment;

import net.splitcells.dem.Dem;

/**
 * <p>Define a single point of process start and its implicit error handling.</p>
 * <p>It was considered to create a unified handler for any kind of exception, security, memory limit etc..
 * This way one could just define a single point of start and error handling,
 * without having to know what kind of handlers exist.
 * It is considered, that this is not possible for now,
 * as there are 2 types of such handlers:</p>
 * <p>Implicit ones, that work things like {@link Exception} and {@link Dem#config()}.
 * There it is easy to inject this type of handling in any code present without changing it.
 * The setup of such handlers is done implicitly as well without having to know what kind of handlers exist.
 * This can be easily used for user error handling,
 * where for example exceptions are used to abort and report the execution results.
 * Only a single point of error handling is required with no arguments or any kind of no special setup.
 * </p>
 * <p>Explicit ones, need explicit arguments, that are provided by the caller.
 * It is relatively hard to define a single point of any kind of error handling,
 * without knowing what kind of error handling exists.
 * Therefore, injecting explicit error handling is considered hard.
 * See handling of errors for one specific user.</p>
 */
public class Container {
}
