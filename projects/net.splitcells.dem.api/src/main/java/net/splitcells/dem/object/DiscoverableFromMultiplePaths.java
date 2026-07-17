/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.object;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;

public interface DiscoverableFromMultiplePaths {
	Set<List<String>> paths();
}
