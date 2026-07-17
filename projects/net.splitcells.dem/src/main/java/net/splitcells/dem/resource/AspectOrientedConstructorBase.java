/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
public class AspectOrientedConstructorBase<T> implements AspectOrientedConstructor<T> {

    public static <A> AspectOrientedConstructorBase<A> aspectOrientedConstructor() {
        return new AspectOrientedConstructorBase<>();
    }

    private AspectOrientedConstructorBase() {
        
    }
    private List<Function<T, T>> aspects = list();

    @ReturnsThis
    public AspectOrientedConstructorBase<T> withAspect(Function<T, T> aspect) {
        aspects.add(aspect);
        return this;
    }

    public T joinAspects(T arg) {
        T joinedAspects = arg;
        for (final var aspect : aspects) {
            joinedAspects = aspect.apply(joinedAspects);
        }
        return joinedAspects;
    }
}
