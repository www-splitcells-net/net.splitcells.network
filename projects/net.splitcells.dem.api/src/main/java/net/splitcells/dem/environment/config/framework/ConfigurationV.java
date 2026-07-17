/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.config.framework;

import lombok.val;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.tree.Tree;

import java.util.function.BiConsumer;

import static net.splitcells.dem.data.order.Comparators.ASCENDING_STRINGS;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.ExecutionException.execException;

public interface ConfigurationV {
    <T> T configValue(Class<? extends Option<T>> key);

    <T> T configValueTyped(Class<T> key);

    Object configValueUntyped(Object key);

    Set<Class<? extends Option<?>>> keys();

    /**
     * Consumes a certain type of {@link Option} and {@link Option#defaultValue()}.
     *
     * @param type     Type of {@link Option}, that will be processed.
     * @param consumer Function that consumes the corresponding {@link Option} and their values.
     * @param <V>      Type of {@link Option#defaultValue()}, that will be consumed.
     */
    <K extends Class<? extends Option<V>>, V> void consume(K type, BiConsumer<K, V> consumer);

    default Tree serialize() {
        val serialization = tree("Configuration Serialization");
        val sortedKeys = listWithValuesOf(keys());
        sortedKeys.sort((a, b)
                -> ASCENDING_STRINGS.compare(a.getName(), b.getName()));
        sortedKeys.forEach(key -> {
            try {
                val option = key.getDeclaredConstructor().newInstance();
                val value = option.serializeUntyped(configValueUntyped(key));
                value.ifPresent(v -> serialization.withChild(tree(option.name()).withChild(v)));
            } catch (Throwable t) {
                throw execException(t);
            }
        });
        return serialization;
    }
}
