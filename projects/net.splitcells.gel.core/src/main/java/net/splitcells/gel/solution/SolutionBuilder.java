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


public class SolutionBuilder implements DefineDemandAttributes, DefineDemands, DefineSupply, ProblemGenerator, DefineSupplyAttributes {

    private List<Attribute<? extends Object>> demandAttributes = list();
    private List<List<Object>> demands = list();
    private Optional<Database> demandsDatabase = Optional.empty();

    private List<Attribute<? extends Object>> supplyAttributes = list();
    private List<List<Object>> supplies = list();
    private Optional<Database> suppliesDatabase = Optional.empty();

    private Constraint constraint;

    protected SolutionBuilder() {
    }

    public static DefineDemandAttributes defineProblem() {
        return new SolutionBuilder();
    }

    @Override
    public Problem toProblem() {
        final var problemsDemands = demandsDatabase.orElseGet(() -> {
            final var d = Databases.database(DEMANDS.value(), null, demandAttributes);
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
                        Solution.class.getSimpleName()
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
    public DefineSupply withSupplies(List<Object>... supplies) {
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
    public DefineSupply withSupplies(List<List<Object>> supplies) {
        this.supplies = supplies;
        return this;
    }

    @Override
    public DefineDemands withDemands(List<List<Object>> supplies) {
        this.demands = supplies;
        return this;
    }

}
