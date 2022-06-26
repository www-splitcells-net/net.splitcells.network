/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.common.Language.*;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;

import static net.splitcells.gel.problem.ProblemI.problem;

import java.util.Arrays;
import java.util.Optional;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.constraint.Constraint;
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
        final var problemsDemands = demandsDatabase.orElseGet(() -> {
            // TODO The demands name is a hack.
            final var d = Databases.database(name.orElse(DEMANDS.value()), null, demandAttributes);
            demands.forEach(demand -> d.addTranslated(demand));
            return d;
        });
        final var problemsSupplies = suppliesDatabase.orElseGet(() -> {
            final var s = Databases.database(SUPPLIES.value(), null, supplyAttributes);
            supplies.forEach(supply -> s.addTranslated(supply));
            return s;
        });
        return problem(
                allocations(
                        name.orElse(Solution.class.getSimpleName())
                        , problemsDemands
                        , problemsSupplies)
                , constraint);
    }

    @Override
    public ProblemGenerator withConstraint(Constraint constraint) {
        this.constraint = constraint;
        return this;
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
        this.demands.addAll(Arrays.asList(demands));
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