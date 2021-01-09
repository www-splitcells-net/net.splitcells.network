package net.splitcells.gel.solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.Language.*;
import static net.splitcells.gel.data.piešķiršanas.Piešķiršanass.piešķiršanas;

import static net.splitcells.gel.data.datubāze.DatuBāzes.datuBāze;
import static net.splitcells.gel.problem.ProblemI.problēma;

import java.util.Arrays;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.problem.DefineDemands;
import net.splitcells.gel.problem.DefineSupply;
import net.splitcells.gel.problem.Define_Demand_Attributes;
import net.splitcells.gel.problem.Problem;
import net.splitcells.gel.problem.ProblemGenerator;


public class SolutionBuilder implements Define_Demand_Attributes, DefineDemands, DefineSupply, ProblemGenerator {

    private List<Atribūts<? extends Object>> prasības_atribūti = list();
    private List<List<Object>> prasības = list();

    private List<Atribūts<? extends Object>> piedāvājumu_atribūti = list();
    private List<List<Object>> piedāvājumi = list();

    private Ierobežojums ierobežojums;

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
    public ProblemGenerator arIerobežojumu(Ierobežojums ierobežojums) {
        this.ierobežojums = ierobežojums;
        return this;
    }

    @Override
    public DefineSupply arPiedāvumuNosaukumiem(Atribūts<? extends Object>... argPiedāvājumuAtribūti) {
        piedāvājumu_atribūti = list(argPiedāvājumuAtribūti).mapped(a -> (Atribūts<Object>) a);
        return this;
    }

    @Override
    public DefineDemands arPrasībasNosaukumiem(Atribūts<? extends Object>... argPrāsibasPiedāvājums) {
        prasības_atribūti = list(argPrāsibasPiedāvājums).mapped(a -> (Atribūts<Object>) a);
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
    public DefineSupply arPiedāvumuNosaukumiem(List<Atribūts<? extends Object>> argPiedāvājumuAtribūti) {
        piedāvājumu_atribūti = argPiedāvājumuAtribūti.mapped(a -> (Atribūts<Object>) a);
        return this;
    }

    @Override
    public DefineDemands arPrasībasNosaukumiem(List<Atribūts<? extends Object>> argPrasībasAtribūti) {
        prasības_atribūti = argPrasībasAtribūti.mapped(a -> (Atribūts<Object>) a);
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
