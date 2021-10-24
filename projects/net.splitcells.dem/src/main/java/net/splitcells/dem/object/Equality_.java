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
package net.splitcells.dem.object;

/**
 * This is an alternative to {@link Object#equals(Object)}.
 * It has the benefit of being typed.
 *
 * Every instance implementing this method should have consistent {@link#equalCOntents}
 * {@Object#equals} and {@Object#hashCode}.
 *
 * Here, no fish will be forced to fly and no bird will be forced to swim. - Motto of an Ottoman school?
 */
public interface Equality_<T> {
    <A extends T> boolean equalContents(A arg);
}
