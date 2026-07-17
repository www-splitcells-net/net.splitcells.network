/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment.resource;

import net.splitcells.dem.environment.config.framework.OptionImpl;

import java.util.function.Supplier;

/**
 * @param <T> Type Of Resource
 * @deprecated Use {@link ResourceOption} interface directly instead.
 */
public class ResourceOptionImpl<T extends Resource> extends OptionImpl<T> implements ResourceOption<T> {
    public ResourceOptionImpl(Supplier<T> argDefaultValue) {
        super(argDefaultValue);
    }
}
