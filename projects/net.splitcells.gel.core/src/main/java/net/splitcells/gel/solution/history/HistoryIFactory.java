/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution.history;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.solution.Solution;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.resource.AspectOrientedConstructorBase.aspectOrientedConstructor;
import static net.splitcells.dem.resource.ConnectingConstructorI.connectingConstructor;
import static net.splitcells.gel.solution.history.HistoryI.historyI;

public class HistoryIFactory implements HistoryFactory {

    private final AspectOrientedConstructorBase<History> aspects = aspectOrientedConstructor();
    private final ConnectingConstructor<History> connectors = connectingConstructor();

    @Override
    public History history(Solution solution) {
        return connectors.connect(aspects.joinAspects(historyI(solution)));
    }

    @Override
    public void close() {
        // Nothing needs to be done.
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }

    @Override public AspectOrientedConstructor<History> withAspect(Function<History, History> aspect) {
        aspects.withAspect(aspect);
        return this;
    }

    @Override public History joinAspects(History arg) {
        return aspects.joinAspects(arg);
    }

    @Override public ConnectingConstructor<History> withConnector(Consumer<History> connector) {
        connectors.withConnector(connector);
        return this;
    }

    @Override public History connect(History subject) {
        return connectors.connect(subject);
    }
}
