package net.splitcells.gel.solution;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.Language.*;
import static net.splitcells.gel.data.piešķiršanas.Piešķiršanass.piešķiršanas;

import static net.splitcells.gel.data.datubāze.DatuBāzes.datuBāze;
import static net.splitcells.gel.problem.ProblēmaI.problēma;

import java.util.Arrays;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.problem.DefinēPrasība;
import net.splitcells.gel.problem.DefinēPiedāvājumi;
import net.splitcells.gel.problem.Definē_prasības_nosaukumu;
import net.splitcells.gel.problem.Problēma;
import net.splitcells.gel.problem.ProblēmaĢenerators;


public class AtrisinājumBūvētājs implements Definē_prasības_nosaukumu, DefinēPrasība, DefinēPiedāvājumi, ProblēmaĢenerators {

    private List<Atribūts<? extends Object>> prasības_atribūti = list();
    private List<List<Object>> prasības = list();

    private List<Atribūts<? extends Object>> piedāvājumu_atribūti = list();
    private List<List<Object>> piedāvājumi = list();

    private Ierobežojums ierobežojums;

    protected AtrisinājumBūvētājs() {
    }

    public static Definē_prasības_nosaukumu definē_problēmu() {
        return new AtrisinājumBūvētājs();
    }

    @Override
    public Problēma uzProblēmu() {
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
    public ProblēmaĢenerators arIerobežojumu(Ierobežojums ierobežojums) {
        this.ierobežojums = ierobežojums;
        return this;
    }

    @Override
    public DefinēPiedāvājumi arPiedāvumuNosaukumiem(Atribūts<? extends Object>... argPiedāvājumuAtribūti) {
        piedāvājumu_atribūti = list(argPiedāvājumuAtribūti).mapped(a -> (Atribūts<Object>) a);
        return this;
    }

    @Override
    public DefinēPrasība arPrasībasNosaukumiem(Atribūts<? extends Object>... argPrāsibasPiedāvājums) {
        prasības_atribūti = list(argPrāsibasPiedāvājums).mapped(a -> (Atribūts<Object>) a);
        return this;
    }

    @Override
    public DefinēPiedāvājumi arePiedāvājumiem(List<Object>... peidāvājumi) {
        piedāvājumi = listWithValuesOf(peidāvājumi);
        return this;
    }

    @Override
    public DefinēPrasība arPrasībam(List<Object> prasība, List<Object>... parsības) {
        prasības = list();
        prasības.add(prasība);
        prasības.addAll(Arrays.asList(parsības));
        return this;
    }

    @Override
    public DefinēPiedāvājumi arPiedāvumuNosaukumiem(List<Atribūts<? extends Object>> argPiedāvājumuAtribūti) {
        piedāvājumu_atribūti = argPiedāvājumuAtribūti.mapped(a -> (Atribūts<Object>) a);
        return this;
    }

    @Override
    public DefinēPrasība arPrasībasNosaukumiem(List<Atribūts<? extends Object>> argPrasībasAtribūti) {
        prasības_atribūti = argPrasībasAtribūti.mapped(a -> (Atribūts<Object>) a);
        return this;
    }

    @Override
    public DefinēPiedāvājumi arePiedāvājumiem(List<List<Object>> peidāvājumi) {
        this.piedāvājumi = peidāvājumi;
        return this;
    }

    @Override
    public DefinēPrasība arPrasībam(List<List<Object>> parsības) {
        this.prasības = parsības;
        return this;
    }

}
