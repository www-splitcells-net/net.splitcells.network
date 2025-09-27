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
package net.splitcells.website.server.client;

import java.util.Optional;
import java.util.function.Function;

/**
 * This represents a Browser element, that may or may not exist.
 */
public interface Element {
    /**
     *
     * @param evaluation This {@link Function} is executed given this {@link Element},
     *                   if the underlying Browser element actually exists.
     * @param <T>        The return type of the evaluation.
     * @return Returns {@link Optional#empty()}, if this {@link Element} does not exist.
     * Otherwise, this returns the result of the evaluation.
     */
    <T> Optional<T> evalIfExists(Function<Element, T> evaluation);

    /**
     * This method only works, if the element actually exists.
     * Calling this method, asserts the existence of this Browser element.
     *
     * @see {@link #evalIfExists(Function)}
     */
    void click();

    /**
     * This method only works, if the element actually exists.
     * Calling this method, asserts the existence of this Browser element.
     *
     * @see {@link #evalIfExists(Function)}
     */
    String textContent();

    /**
     * This method only works, if the element actually exists.
     * Calling this method, asserts the existence of this Browser element.
     *
     * @see {@link #evalIfExists(Function)}
     */
    String value();
}
