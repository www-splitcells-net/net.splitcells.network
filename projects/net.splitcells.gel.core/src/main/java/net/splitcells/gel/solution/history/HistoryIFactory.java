/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history;

import net.splitcells.gel.solution.Solution;

import static net.splitcells.gel.solution.history.HistoryI.historyI;

public class HistoryIFactory implements HistoryFactory {

    @Override
    public History history(Solution solution) {
        return historyI(solution);
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }
}
