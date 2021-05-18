package net.splitcells.gel.solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.common.Language.*;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;

import static net.splitcells.gel.problem.ProblemI.problem;

import java.util.Arrays;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.DefineDemands;
import net.splitcells.gel.problem.DefineSupply;
import net.splitcells.gel.problem.Define_Demand_Attributes;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.problem.ProblemGenerator;


public class SolutionBuilder implements Define_Demand_Attributes, DefineDemands, DefineSupply, ProblemGenerator {

    private List<Attribute<? extends Object>> demandAttributes = list();
    private List<List<Object>> demands = list();

    private List<Attribute<? extends Object>> supplyAttributes = list();
    private List<List<Object>> supplies = list();

    private Constraint constraint;

    protected SolutionBuilder() {
    }

    public static Define_Demand_Attributes defineProblem() {
        return new SolutionBuilder();
    }

    @Override
    public Problem toProblem() {
        final var demandDatabase = Databases.database(DEMANDS.value(), null, demandAttributes);
        final var supplyDatabase = Databases.database(SUPPLIES.value(), null, supplyAttributes);
        demands.forEach(demand -> demandDatabase.addTranslated(demand));
        supplies.forEach(supplies -> supplyDatabase.addTranslated(supplies));
        return problem(
                allocations(
                        Solution.class.getSimpleName()
                        , demandDatabase
                        , supplyDatabase)
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
    public DefineDemands withDemandAttributes(List<Attribute<? extends Object>> demandAttributes) {
        this.demandAttributes = demandAttributes.mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineSupply withDemands(Database demands) {
        throw notImplementedYet();
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
