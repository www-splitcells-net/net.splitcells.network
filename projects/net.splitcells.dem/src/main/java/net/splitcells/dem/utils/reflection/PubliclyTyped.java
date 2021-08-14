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
package net.splitcells.dem.utils.reflection;

import java.util.Optional;

import static net.splitcells.dem.utils.reflection.ClassesRelated.isSubClass;

public interface PubliclyTyped<T> {

    Class<? extends T> type();

    @SuppressWarnings("unchecked")
	default <R> Optional<R> casted(Class<? extends R> targetType) {
        if (isSubClass(targetType, type())) {
            return Optional.of((R) this);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
	default <R> R cast(Class<? extends R> targetType) {
        if (isSubClass(targetType, type())) {
            return (R) this;
        }
        throw new IllegalArgumentException(targetType.toString());
    }
}
