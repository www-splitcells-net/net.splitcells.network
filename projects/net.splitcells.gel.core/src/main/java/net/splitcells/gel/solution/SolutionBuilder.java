/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.solution;

import static net.splitcells.dem.Dem.configValue;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.object.Discoverable.discoverable;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.common.Language.*;
import static net.splitcells.gel.constraint.QueryI.query;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.data.assignment.Assignmentss.assignments;

import static net.splitcells.gel.problem.ProblemI.problem;

import java.util.Optional;
import java.util.function.Function;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.ProgramsDiscoveryPath;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.Query;
import net.splitcells.gel.constraint.QueryI;
import net.splitcells.gel.data.assignment.Assignments;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.*;


public class SolutionBuilder implements DefineDemandAttributes, DefineDemands, DefineSupply, ProblemGenerator, DefineSupplyAttributes, DefineConstraints {

    private List<Attribute<? extends Object>> demandAttributes = list();
    private List<List<Object>> demands = list();
    private Optional<Database> demandsDatabase = Optional.empty();

    private List<Attribute<? extends Object>> supplyAttributes = list();
    private List<List<Object>> supplies = list();
    private Optional<Database> suppliesDatabase = Optional.empty();
    /**
     * Without a good name, it can get very hard to distinguish {@link Solution}
     * instances from one another at one glance.
     * It does not take much time to define a name for a {@link Solution},
     * but it can take much time to manage multiple such instances at once
     * without having names,
     * especially when a {@link Solution} is the demand or supply of an other {@link Solution}
     */
    private Optional<String> name = Optional.empty();

    private Constraint constraint;
    private Assignments assignments;

    protected SolutionBuilder(String name) {
        this.name = Optional.of(name);
    }

    protected SolutionBuilder() {
    }

    @Deprecated
    public static DefineDemandAttributes defineProblem() {
        return new SolutionBuilder();
    }

    public static DefineDemandAttributes defineProblem(String name) {
        return new SolutionBuilder(name);
    }

    @Override
    public Problem toProblem() {
        return problem(assignments, constraint);
    }

    private void initAllocations() {
        final var assignmentsName = name.orElse(Solution.class.getSimpleName());
        final var problemsDemands = demandsDatabase.orElseGet(() -> {
            final var d = Databases.database(DEMANDS.value()
                    , () -> configValue(ProgramsDiscoveryPath.class).path().withAppended(assignmentsName)
                    , demandAttributes);
            demands.forEach(demand -> d.addTranslated(demand));
            return d;
        });
        final var problemsSupplies = suppliesDatabase.orElseGet(() -> {
            final var s = Databases.database(SUPPLIES.value()
                    , () -> configValue(ProgramsDiscoveryPath.class).path().withAppended(assignmentsName)
                    , supplyAttributes);
            supplies.forEach(supply -> s.addTranslated(supply));
            return s;
        });
        assignments = assignments("solution", problemsDemands, problemsSupplies);
    }

    @Override
    public ProblemGenerator withConstraint(Constraint constraint) {
        initAllocations();
        this.constraint = constraint;
        return this;
    }

    @Override
    public ProblemGenerator withConstraint(Function<Query, Query> builder) {
        initAllocations();
        final var path = Lists.<String>list();
        if (demandsDatabase.isPresent()) {
            path.withAppended(demandsDatabase.get().path());
        }
        if (name.isPresent()) {
            path.withAppended(name.get());
        }
        this.constraint = builder.apply(query(forAll(Optional.of(discoverable(path))), assignments)).currentConstraint();
        return this;
    }

    @Override
    public ProblemGenerator withConstraints(List<Function<Query, Query>> builders) {
        initAllocations();
        final var root = forAll();
        builders.forEach(b -> b.apply(query(root, assignments)));
        return withConstraint(root);
    }

    @Override
    public DefineSupply withSupplyAttributes(Attribute<? extends Object>... supplyAttributes) {
        this.supplyAttributes = list(supplyAttributes).mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineDemands withDemandAttributes(Attribute<? extends Object>... demandAttributes) {
        this.demandAttributes = list(demandAttributes).mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineDemands withDemandAttributes2(List<Attribute<Object>> demandAttributes) {
        this.demandAttributes = list(demandAttributes).mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineConstraints withSupplies(List<Object>... supplies) {
        this.supplies = listWithValuesOf(supplies);
        return this;
    }

    @Override
    public DefineDemands withDemands(List<Object> demand, List<Object>... demands) {
        this.demands = list();
        this.demands.add(demand);
        this.demands.addAll(list(demands));
        return this;
    }

    @Override
    public DefineDemands withDemands(List<List<Object>> demands) {
        this.demands = demands;
        return this;
    }

    @Override
    public DefineSupply withSupplyAttributes(List<Attribute<? extends Object>> supplyAttributes) {
        this.supplyAttributes = supplyAttributes.mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineSupply withSupplyAttributes2(List<Attribute<Object>> supplyAttributes) {
        this.supplyAttributes = supplyAttributes.mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineDemands withDemandAttributes(List<Attribute<? extends Object>> demandAttributes) {
        this.demandAttributes = demandAttributes.mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineSupplyAttributes withDemands(Database demands) {
        demandsDatabase = Optional.of(demands);
        return this;
    }

    @Override
    public DefineConstraints withSupplies(List<List<Object>> supplies) {
        this.supplies = supplies;
        return this;
    }

    @Override
    public DefineConstraints withSupplies(Database supplies) {
        suppliesDatabase = Optional.of(supplies);
        return this;
    }

}