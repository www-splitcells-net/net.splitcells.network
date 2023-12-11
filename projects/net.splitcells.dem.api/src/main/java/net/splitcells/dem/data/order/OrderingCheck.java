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
package net.splitcells.dem.data.order;

/**
 * Provides functions in order to check the ordering of this and other values.
 * `{@link Object#equals}` is part of the interface implicitly.
 *
 * @author splitcells
 */
public interface OrderingCheck<T> {

    boolean smallerThan(T other);

    boolean smallerThanOrEqual(T other);

    boolean greaterThan(T other);

    boolean greaterThanOrEqual(T other);

    boolean equalz(T other);

}
