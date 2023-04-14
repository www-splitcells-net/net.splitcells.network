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
package net.splitcells.dem.data;

import net.splitcells.dem.resource.AspectOrientedConstructor;

/**
 * An {@link Identifiable} has a method, that returns the identity of the object,
 * whose {@link Object#equals(Object)} and {@link Object#hashCode()} can be used for comparing instances of {@link Identifiable}.
 * It is a replacement for {@code this}, as {@code this} is needed for aspects(see {@link AspectOrientedConstructor}) and wrappers,
 * as otherwise the {@link Object#equals(Object)} method could not be implemented in such wrappers correctly.
 */
public interface Identifiable {
    Object identity();
}
