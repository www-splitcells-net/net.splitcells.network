/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.framework;

import java.util.function.Predicate;

public interface RaterPredicate<T> extends Predicate<T> {
    String descriptivePathName();
}
