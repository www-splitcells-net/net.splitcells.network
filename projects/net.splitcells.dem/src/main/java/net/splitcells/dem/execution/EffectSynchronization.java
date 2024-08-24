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
package net.splitcells.dem.execution;

import java.util.function.Function;

/**
 * <p>This annotation is used, in order to mark a method call as synchronization code,
 * when an object is wrapped with {@link Effect} and {@link net.splitcells.dem.resource.AspectOrientedConstructor#withAspect(Function)}.
 * This annotation is the recommended way to mark such code for documentation purposes,
 * as some code cannot be synchronized implicitly without a none purpose function call.
 * For example, any method returning void could be executed in parallel by {@link Effect} by default,
 * but sometimes such code needs to be run synchronously.
 * There is currently no other way to force a synchronization as to call another method,
 * that returns anything and thereby forces synchronization implicitly.
 * </p>
 */
public @interface EffectSynchronization {
}
