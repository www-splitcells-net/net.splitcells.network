/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.assignment;

import net.splitcells.dem.resource.AspectOrientedConstructor;
import net.splitcells.dem.resource.AspectOrientedConstructorBase;
import net.splitcells.dem.resource.ConnectingConstructor;
import net.splitcells.gel.data.table.Table;

import java.util.function.Consumer;
import java.util.function.Function;

import static net.splitcells.dem.resource.AspectOrientedConstructorBase.aspectOrientedConstructor;
import static net.splitcells.dem.resource.ConnectingConstructorI.connectingConstructor;

public class AssignmentsIFactory implements AssignmentsFactory {

    private final AspectOrientedConstructorBase<Assignments> aspects = aspectOrientedConstructor();
    private final ConnectingConstructor<Assignments> connector = connectingConstructor();

    @Override
    public void close() {
        // Nothing needs to be done.
    }

    @Override
    public void flush() {
        // Nothing needs to be done.
    }

    @Override
    public Assignments assignments(String name, Table demands, Table supplies) {
        return AssignmentsI.assignments(name, demands, supplies);
    }

    @Override
    public AspectOrientedConstructor withAspect(Function aspect) {
        return aspects.withAspect(aspect);
    }

    @Override
    public Assignments joinAspects(Assignments arg) {
        return aspects.joinAspects(arg);
    }

    @Override public ConnectingConstructor<Assignments> withConnector(Consumer<Assignments> argConnector) {
        this.connector.withConnector(argConnector);
        return this;
    }

    @Override public Assignments connect(Assignments subject) {
        return connector.connect(subject);
    }
}
