/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.Returns_this;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public abstract class AspectOrientedConstructor<T> {
    private List<Function<T, T>> aspects = list();

    @Returns_this
    public AspectOrientedConstructor<T> withAspect(Function<T, T> aspect) {
        aspects.add(aspect);
        return this;
    }

    protected T joinAspects(T arg) {
        T joinedAspects = arg;
        for (final var aspect : aspects) {
            joinedAspects = aspect.apply(joinedAspects);
        }
        return joinedAspects;
    }
}
