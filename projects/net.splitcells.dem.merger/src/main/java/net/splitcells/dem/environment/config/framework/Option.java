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
package net.splitcells.dem.environment.config.framework;

/**
 * Every implementation has to have a public default constructor.
 * <p>
 * TODO Check if every option has public constructor without arguments.
 * <p>
 * TODO Generate dedicated Option documentation for website.
 */
public interface Option<T extends Object> {
    T defaultValue();
}
