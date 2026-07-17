/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history;

import net.splitcells.dem.environment.resource.ResourceOptionImpl;
import net.splitcells.gel.solution.Solution;

import static net.splitcells.dem.Dem.environment;

public class Histories extends ResourceOptionImpl<HistoryFactory> {
    public Histories() {
        super(() -> new HistoryIFactory());
    }

    public static History history(Solution solution) {
        return environment().config().configValue(Histories.class).history(solution);
    }
}
