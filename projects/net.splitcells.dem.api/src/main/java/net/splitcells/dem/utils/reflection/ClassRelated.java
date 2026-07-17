/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.reflection;

import net.splitcells.dem.data.set.list.List;

public interface ClassRelated {

    List<Class<?>> allClasses();

    List<Class<?>> allClassesOf(String rootPackage);

    String simplifiedName(Class<?> arg);

}
