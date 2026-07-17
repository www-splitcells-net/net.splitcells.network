/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.Flow;
import net.splitcells.dem.data.Flows;
import net.splitcells.dem.lang.annotations.JavaLegacy;

import java.util.ArrayList;
import java.util.Collection;

@JavaLegacy
public class ListI<T> extends ArrayList<T> implements List<T> {
    public static <T> List<T> _list() {
        return new ListI<>();
    }

    public static <T> List<T> _list(Collection<T> startContent) {
        return new ListI<>(startContent);
    }

    private ListI() {
    }
    
    private ListI(Collection<T> startContent) {
        super(startContent);
    }

    @Override
    public void prepareForSizeOf(int targetSize) {
        this.ensureCapacity(targetSize * 2);
    }

    @Override
    public Flow<T> stream() {
        return Flows.flow(super.stream());
    }
}
