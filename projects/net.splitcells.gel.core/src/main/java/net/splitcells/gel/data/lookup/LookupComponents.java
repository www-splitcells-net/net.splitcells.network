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
package net.splitcells.gel.data.lookup;

import java.util.function.Predicate;

import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;
import net.splitcells.gel.data.view.column.Column;

public interface LookupComponents<T> {
    /**
     * @param value The value, that each selected line should contain.
     * @return Returns a {@link View}, where one or more {@link Column} contain only the provided value.
     * The choice of {@link Column} for this is determined by the implementor.
     * The returned {@link View} is automatically updated,
     * when the underlying {@link View} is updated.
     */
    View persistedLookup(T value);

    /**
     * @param predicate States which lines to select by returning true for such.
     * @return Returns a {@link View}, where all {@link Line} comply with the predicate.
     * The returned {@link View} is automatically updated,
     * when the underlying {@link View} is updated.
     */
    View persistedLookup(Predicate<T> predicate);

    /**
     * @param value The value, that each selected line should contain.
     * @return Returns a {@link View}, where one or more {@link Column} contain only the provided value.
     * The choice of {@link Column} for this is determined by the implementor.
     * The returned {@link View} is automatically updated **or not**,
     * when the underlying {@link View} is updated.
     * The caller states, that this lookup is used only until the underlying {@link View} is not used.
     */
    default View lookup(T value) {
        return persistedLookup(value);
    }

    /**
     * @param predicate States which lines to select by returning true for such.
     * @return Returns a {@link View}, where all {@link Line} comply with the predicate.
     * The returned {@link View} is automatically updated **or not**,
     * when the underlying {@link View} is updated.
     * The caller states, that this lookup is used only until the underlying {@link View} is not used.
     */
    default View lookup(Predicate<T> predicate) {
        return persistedLookup(predicate);
    }
}
