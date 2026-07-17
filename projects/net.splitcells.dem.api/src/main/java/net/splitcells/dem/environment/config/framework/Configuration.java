/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

/**
 * TODO Split up into generic dynamically typed and type safe table and this configuration class.
 */
public interface Configuration extends ConfigurationV {

    @ReturnsThis
    <T> Configuration withInitedOption(Class<? extends Option<T>> key);

    @ReturnsThis
    <T> Configuration withConfigValue(Class<? extends Option<T>> key, T value);

    <T> boolean isConfigInitialized(Class<? extends Option<T>> key);

    /**
     * This helper method makes it easier to write and read long configuration chains.
     *
     * @param configurator
     * @return
     */
    @ReturnsThis default Configuration configure(Consumer<Configuration> configurator) {
        configurator.accept(this);
        return this;
    }

    /**
     * Process a certain type of resource values.
     *
     * @param type      Type of resource values, that will be processed.
     * @param processor Function that replaces current resource values.
     * @param <T>       Type of resource values, that will be processed.
     */
    <T> void process(Class<? extends T> type, Function<T, T> processor);
}
