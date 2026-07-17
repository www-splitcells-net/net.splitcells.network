/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.network.worker.via.java.repo;

import net.splitcells.dem.lang.annotations.ReturnsThis;

public interface Repository {
    @ReturnsThis
    Repository commitAll();
}
