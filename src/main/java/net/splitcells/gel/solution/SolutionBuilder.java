package net.splitcells.gel.solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.Language.*;
import static net.splitcells.gel.data.allocation.Allocationss.piešķiršanas;

import static net.splitcells.gel.data.database.Databases.datuBāze;
import static net.splitcells.gel.problem.ProblemI.problēma;

import java.util.Arrays;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.problem.DefineDemands;
import net.splitcells.gel.problem.DefineSupply;
import net.splitcells.gel.problem.Define_Demand_Attributes;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.problem.ProblemGenerator;


public class SolutionBuilder implements Define_Demand_Attributes, DefineDemands, DefineSupply, ProblemGenerator {

    private List<Attribute<? extends Object>> prasības_atribūti = list();
    private List<List<Object>> prasības = list();

    private List<Attribute<? extends Object>> piedāvājumu_atribūti = list();
    private List<List<Object>> piedāvājumi = list();

    private Constraint ierobežojums;

    protected SolutionBuilder() {
    }

    public static Define_Demand_Attributes definē_problēmu() {
        return new SolutionBuilder();
    }

    @Override
    public Problem uzProblēmu() {
        final var prasībasDatuBaže = datuBāze(PRASĪBAS.apraksts(), null, prasības_atribūti);
        final var piedāvājumuDatuBaže = datuBāze(PIEDĀVĀJUMI.apraksts(), null, piedāvājumu_atribūti);
        prasības.forEach(prasība -> prasībasDatuBaže.pieliktUnPārtulkot(prasība));
        piedāvājumi.forEach(piedāvājums -> piedāvājumuDatuBaže.pieliktUnPārtulkot(piedāvājums));
        return problēma(
                piešķiršanas(
                        Solution.class.getSimpleName()
                        , prasībasDatuBaže
                        , piedāvājumuDatuBaže)
                , ierobežojums);
    }

    @Override
    public ProblemGenerator arIerobežojumu(Constraint ierobežojums) {
        this.ierobežojums = ierobežojums;
        return this;
    }

    @Override
    public DefineSupply arPiedāvumuNosaukumiem(Attribute<? extends Object>... argPiedāvājumuAtribūti) {
        piedāvājumu_atribūti = list(argPiedāvājumuAtribūti).mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineDemands arPrasībasNosaukumiem(Attribute<? extends Object>... argPrāsibasPiedāvājums) {
        prasības_atribūti = list(argPrāsibasPiedāvājums).mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineSupply arePiedāvājumiem(List<Object>... peidāvājumi) {
        piedāvājumi = listWithValuesOf(peidāvājumi);
        return this;
    }

    @Override
    public DefineDemands arPrasībam(List<Object> prasība, List<Object>... parsības) {
        prasības = list();
        prasības.add(prasība);
        prasības.addAll(Arrays.asList(parsības));
        return this;
    }

    @Override
    public DefineSupply arPiedāvumuNosaukumiem(List<Attribute<? extends Object>> argPiedāvājumuAtribūti) {
        piedāvājumu_atribūti = argPiedāvājumuAtribūti.mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineDemands arPrasībasNosaukumiem(List<Attribute<? extends Object>> argPrasībasAtribūti) {
        prasības_atribūti = argPrasībasAtribūti.mapped(a -> (Attribute<Object>) a);
        return this;
    }

    @Override
    public DefineSupply arePiedāvājumiem(List<List<Object>> peidāvājumi) {
        this.piedāvājumi = peidāvājumi;
        return this;
    }

    @Override
    public DefineDemands arPrasībam(List<List<Object>> parsības) {
        this.prasības = parsības;
        return this;
    }

}
