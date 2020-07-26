package net.splitcells.gel.kodols.atrisinājums;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.gel.kodols.Valoda.*;
import static net.splitcells.gel.kodols.dati.piešķiršanas.PiešķiršanasI.piešķiršanas;

import static net.splitcells.gel.martins.avots.dati.datubāze.DatuBāzeIRef.datuBāze;

import java.util.Arrays;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.problēma.DefinēPrasība;
import net.splitcells.gel.kodols.problēma.DefinēPiedāvājumi;
import net.splitcells.gel.kodols.problēma.Definē_prasības_nosaukumu;
import net.splitcells.gel.kodols.problēma.Problēma;
import net.splitcells.gel.kodols.problēma.ProblēmaĢenerators;
import net.splitcells.gel.kodols.problēma.ProblēmaI;


public class Atrisinājumi implements Definē_prasības_nosaukumu, DefinēPrasība, DefinēPiedāvājumi, ProblēmaĢenerators {

    private List<Atribūts<? extends Object>> prasības_atribūti = list();
    private List<List<Object>> prasības = list();

    private List<Atribūts<? extends Object>> piedāvājumu_atribūti = list();
    private List<List<Object>> piedāvājumi = list();

    private Ierobežojums ierobežojums;

    protected Atrisinājumi() {
    }

    public static Definē_prasības_nosaukumu definē_problēmu() {
        return new Atrisinājumi();
    }

    @Override
    public Problēma uzProblēmu() {
        final var prasībasDatuBaže = datuBāze(PRASĪBAS.apraksts(), null, prasības_atribūti);
        final var piedāvājumuDatuBaže = datuBāze(PIEDĀVĀJUMI.apraksts(), null, piedāvājumu_atribūti);
        prasības.forEach(prasība -> prasībasDatuBaže.pieliktUnPārtulkot(prasība));
        piedāvājumi.forEach(piedāvājums -> piedāvājumuDatuBaže.pieliktUnPārtulkot(piedāvājums));
        return new ProblēmaI(
                piešķiršanas(
                        Atrisinājums.class.getSimpleName()
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
