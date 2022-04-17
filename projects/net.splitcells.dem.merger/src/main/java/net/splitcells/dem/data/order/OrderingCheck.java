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
package net.splitcells.dem.data.order;

/**
 * Provides functions in order to check the ordering of {@link this} and an
 * other values.
 * `{@link Object#equals}` is part of the interface implicitly.
 * 
 * @author splitcells
 *
 */
public interface OrderingCheck<T> {

	boolean smallerThan(T other);

	boolean smallerThanOrEqual(T other);

	boolean greaterThan(T other);

	boolean greaterThanOrEqual(T other);

	boolean equalz(T other);

}
