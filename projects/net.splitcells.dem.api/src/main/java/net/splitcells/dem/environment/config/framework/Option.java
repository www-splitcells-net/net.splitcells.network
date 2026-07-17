/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.Dem;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.object.Convertible;

import java.util.Optional;

/**
 * <p>Every implementation has to have a public default constructor and needs to be thread safe.</p>
 * <p>Implementations of option values should have factory methods starting with an underscore,
 * in order to discourage their usage.
 * Access to dependencies should be done via {@link Option} and
 * {@link Dem#config()} instead of directly accessing their implementations,
 * as otherwise swapping the dependencies implementation becomes a lot harder.</p>
 * <p>
 * TODO Check if every option has public constructor without arguments.
 * <p>
 * TODO Generate dedicated Option documentation for website.
 */
public interface Option<T extends Object> {
    T defaultValue();
    
    /**
     * IDEA Use {@link Convertible}. In the past this it not worth to use it yet,
     * but {@link Convertible} would be a lot more portable and more fitting from a technical point of view.
     * On the other hand, {@link Convertible} would require some extensions in order to support nested data structures.
     *  
     * @return
     */
    Optional<Tree> serialize(T currentValue);

    /**
     * This method makes it easier to handle the genric types.
     * Using {@link #serialize(T)} instead, is recommended where possible.
     * 
     * @param currentValue
     * @return
     */
    default Optional<Tree> serializeUntyped(Object currentValue)  {
        return serialize((T) currentValue);
    }
    
    default String name() {
        return getClass().getName();
    }
}
