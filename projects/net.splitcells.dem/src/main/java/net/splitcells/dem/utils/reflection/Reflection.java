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

import net.splitcells.dem.lang.annotations.ReturnsThis;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * <p>
 * This package contains all complex reflection code.
 * Complex reflection code should not be used anywhere used.
 * </p>
 * <p>
 * There some execptions:
 * <ol>
 *     <li>{@link net.splitcells.dem.environment.config.framework.Configuration}</li>
 *     <li>Return value casts of methods, that are annoted with {@link ReturnsThis}</li>
 * </ol>
 */
public final class Reflection {
    private Reflection() {
        throw constructorIllegal();
    }
}
