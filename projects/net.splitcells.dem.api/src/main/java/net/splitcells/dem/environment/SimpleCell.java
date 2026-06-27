/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment;

import lombok.val;



import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.reflection.ClassesRelated.simplifiedName;

/**
 * This is a {@link Cell}, where nothing has to be implemented and can be used as a feature flag,
 * for simple configurations.
 */
public interface SimpleCell extends Cell {
    default String groupId() {
        val path = simplifiedName(getClass()).split("\\.");
        return path[path.length - 1];
    }

    default String artifactId() {
        val path = list(simplifiedName(getClass()).split("\\."));
        return path.removeLast();
    }
}
