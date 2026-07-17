/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.lang.tree.Tree;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * This template simplifies the implementation of new options.
 *
 * @param <V> Type of the options value.
 * @deprecated This class is deprecated,
 * because there is no need to determine the default value via the constructor.
 */
@Deprecated
public class OptionImpl<V> implements Option<V> {

    private final Supplier<V> defaultValue;

    public OptionImpl(Supplier<V> argDefaultValue) {
        defaultValue = argDefaultValue;
    }

    @Override
    public V defaultValue() {
        return defaultValue.get();
    }

    @Override public Optional<Tree> serialize(V currentValue) {
        return Optional.empty();
    }

}
