package net.splitcells.gel.kodols.dati.piešķiršanas;

import static java.util.Objects.requireNonNull;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.concat;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Map;
import java.util.Set;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.kodols.dati.tabula.kolonna.KolonnaSkats;
import org.w3c.dom.Element;
import net.splitcells.gel.kodols.dati.tabula.kolonna.Kolonna;
import net.splitcells.gel.kodols.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāzeI;
import net.splitcells.gel.kodols.dati.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;

public class PiešķiršanasI implements Piešķiršanas {
    protected final String vārds;
    protected final DatuBāze piešķiršanas;

    protected final List<PapildinājumsKlausītājs> papildinājumsKlausītājs = list();
    protected final List<PirmsNoņemšanasKlausītājs> primsNoņemšanaAbonēšanas = list();
    protected final List<PirmsNoņemšanasKlausītājs> pēcNoņemšanaAbonēšanas = list();

    protected final DatuBāze piedāvājumi;
    protected final DatuBāze piedāvājumi_lietoti;
    protected final DatuBāze piedāvājumi_nelietoti;

    protected final DatuBāze prāsibas;
    protected final DatuBāze prāsibas_lietoti;
    protected final DatuBāze prāsibas_nelietoti;

    protected final Map<Integer, Integer> piešķiršanasIndekss_uz_lietotuPrāsibuIndekss = map();
    protected final Map<Integer, Integer> piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss = map();

    protected final Map<Integer, Set<Integer>> lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu = map();
    protected final Map<Integer, Set<Integer>> lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu = map();

    protected final Map<Integer, Set<Integer>> lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu = map();
    protected final Map<Integer, Set<Integer>> lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu = map();

    @Deprecated
    protected PiešķiršanasI(String vārds, DatuBāze prasības, DatuBāze piedāvājumi) {
        this.vārds = vārds;
        piešķiršanas = new DatuBāzeI("piešķiršanas", this, concat(prasības.nosaukumuSkats(), piedāvājumi.nosaukumuSkats()));
        // DARĪT Noņemiet kodu un komentāru dublēšanos.
        {
            this.prāsibas = prasības;
            prāsibas_nelietoti = new DatuBāzeI("prasības_nelietoti", this, prasības.nosaukumuSkats());
            prāsibas_lietoti = new DatuBāzeI("prasības_lietoti", this, prasības.nosaukumuSkats());
            prasības.jēlaRindasSkats().forEach(prāsibas_nelietoti::pielikt);
            prasības.abonē_uz_papildinājums(prāsibas_nelietoti::pielikt);
            prasības.abonē_uz_iepriekšNoņemšana(removalOf -> {
                if (lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.containsKey(removalOf.indekss())) {
                    listWithValuesOf(
                            lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(removalOf.indekss()))
                            .forEach(allocation_of_demand -> noņemt(piešķiršanas.jēlaRindasSkats().get(allocation_of_demand)));
                }
                if (prāsibas_nelietoti.satur(removalOf)) {
                    prāsibas_nelietoti.noņemt(removalOf);
                }
                // SALABOT Vai alternatīvā gadījumā būtu jādara kaut kas cits.
                if (prāsibas_lietoti.satur(removalOf)) {
                    prāsibas_lietoti.noņemt(removalOf);
                }
            });
        }
        {
            this.piedāvājumi = requireNonNull(piedāvājumi);
            piedāvājumi_nelietoti = new DatuBāzeI("piedāvājumi_nelietoti", this, piedāvājumi.nosaukumuSkats());
            piedāvājumi_lietoti = new DatuBāzeI("piedāvājumi_lietoti", this, piedāvājumi.nosaukumuSkats());
            piedāvājumi.jēlaRindasSkats().forEach(piedāvājumi_nelietoti::pielikt);
            piedāvājumi.abonē_uz_papildinājums(i -> {
                piedāvājumi_nelietoti.pielikt(i);
            });
            piedāvājumi.abonē_uz_iepriekšNoņemšana(noņemšanaNo -> {
                if (lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.containsKey(noņemšanaNo.indekss())) {
                    listWithValuesOf
                            (lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(noņemšanaNo.indekss()))
                            .forEach(piešķiršanas_no_piedāvāijumu
                                    -> noņemt(piešķiršanas.jēlaRindasSkats().get(piešķiršanas_no_piedāvāijumu)));
                }
                if (piedāvājumi_nelietoti.satur(noņemšanaNo)) {
                    piedāvājumi_nelietoti.noņemt(noņemšanaNo);
                }
                // SALABOT Vai alternatīvā gadījumā būtu jādara kaut kas cits.
                if (piedāvājumi_lietoti.satur(noņemšanaNo)) {
                    piedāvājumi_lietoti.noņemt(noņemšanaNo);
                }
            });
        }
    }

    @Override
    public DatuBāze piedāvājums() {
        return piedāvājumi;
    }

    @Override
    public DatuBāze piedāvājumi_lietoti() {
        return piedāvājumi_lietoti;
    }

    @Override
    public DatuBāze piedāvājums_nelietots() {
        return piedāvājumi_nelietoti;
    }

    @Override
    public DatuBāze prasība() {
        return prāsibas;
    }

    @Override
    public DatuBāze prasība_lietots() {
        return prāsibas_lietoti;
    }

    @Override
    public DatuBāze prasības_nelietotas() {
        return prāsibas_nelietoti;
    }

    @Override
    public Rinda piešķirt(Rinda prasība, Rinda piedāvājums) {
        final var piešķiršana = piešķiršanas.pieliktUnPārtulkot(Rinda.saķēdet(prasība, piedāvājums));
        if (!lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.containsKey(piedāvājums.indekss())) {
            piedāvājumi_lietoti.pielikt(piedāvājums);
            piedāvājumi_nelietoti.noņemt(piedāvājums);
        }
        if (!lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.containsKey(prasība.indekss())) {
            prāsibas_lietoti.pielikt(prasība);
            prāsibas_nelietoti.noņemt(prasība);
        }
        {
            piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.put(piešķiršana.indekss(), prasība.indekss());
            piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.put(piešķiršana.indekss(), piedāvājums.indekss());
        }
        {
            {
                if (!lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.containsKey(prasība.indekss())) {
                    lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.put(prasība.indekss(), setOfUniques());
                }
                lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(prasība.indekss()).add(piešķiršana.indekss());
                if (!lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.containsKey(piedāvājums.indekss())) {
                    lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.put(piedāvājums.indekss(), setOfUniques());
                }
                lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(piedāvājums.indekss()).add(piešķiršana.indekss());
            }
        }
        {
            {
                if (!lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.containsKey(prasība.indekss())) {
                    lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.put(prasība.indekss(), setOfUniques());
                }
                lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.get(prasība.indekss()).add(piedāvājums.indekss());
            }
            {
                if (!lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.containsKey(piedāvājums.indekss())) {
                    lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.put(piedāvājums.indekss(), setOfUniques());
                }
                lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.get(piedāvājums.indekss()).add(prasība.indekss());
            }
        }
        papildinājumsKlausītājs.forEach(listener -> listener.reģistrē_papildinājumi(piešķiršana));
        return piešķiršana;
    }

    @Override
    public Rinda prasība_no_piešķiršana(Rinda piešķiršana) {
        return prāsibas.jēlaRindasSkats().get(piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.get(piešķiršana.indekss()));
    }

    @Override
    public Rinda piedāvājums_no_piešķiršana(Rinda allocation) {
        return piedāvājumi.jēlaRindasSkats()
                .get(piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.get(allocation.indekss()));
    }

    @Override
    public Rinda pieliktUnPārtulkot(List<?> vertības) {
        throw not_implemented_yet();
    }

    @Override
    public Rinda pielikt(Rinda rinda) {
        throw not_implemented_yet();
    }

    @Override
    public void noņemt(Rinda piešķiršana) {
        final var prasība = prasība_no_piešķiršana(piešķiršana);
        final var piedāvājums = piedāvājums_no_piešķiršana(piešķiršana);
        primsNoņemšanaAbonēšanas.forEach(pirmsNoņemšanasKlausītājs -> pirmsNoņemšanasKlausītājs.rēgistrē_pirms_noņemšanas(piešķiršana));
        piešķiršanas.noņemt(piešķiršana);
        // TODO Make following code a remove subscription to allocations.
        {
            piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.remove(piešķiršana.indekss());
            piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.remove(piešķiršana.indekss());
        }
        {
            {
                lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.get(prasība.indekss()).remove(piedāvājums.indekss());
                if (lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.get(prasība.indekss()).isEmpty()) {
                    lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.remove(prasība.indekss());
                }
                lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.get(piedāvājums.indekss()).remove(prasība.indekss());
                if (lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.get(piedāvājums.indekss()).isEmpty()) {
                    lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.remove(piedāvājums.indekss());
                }
            }
            {
                lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(piedāvājums.indekss()).remove(piešķiršana.indekss());
                if (lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(piedāvājums.indekss()).isEmpty()) {
                    lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.remove(piedāvājums.indekss());
                }
                lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(prasība.indekss()).remove(piešķiršana.indekss());
                if (lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(prasība.indekss()).isEmpty()) {
                    lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.remove(prasība.indekss());
                }
            }
        }
        piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.remove(piešķiršana.indekss());
        piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.remove(piešķiršana.indekss());
        if (!lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.containsKey(prasība.indekss())) {
            prāsibas_lietoti.noņemt(prasība);
            prāsibas_nelietoti.pielikt(prasība);
        }
        if (!lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.containsKey(piedāvājums.indekss())) {
            piedāvājumi_lietoti.noņemt(piedāvājums);
            piedāvājumi_nelietoti.pielikt(piedāvājums);
        }
        pēcNoņemšanaAbonēšanas.forEach(listener -> listener.rēgistrē_pirms_noņemšanas(piešķiršana));
    }

    @Override
    public void abonē_uz_papildinājums(PapildinājumsKlausītājs klausītājs) {
        papildinājumsKlausītājs.add(klausītājs);
    }

    @Override
    public List<Atribūts<Object>> nosaukumuSkats() {
        return piešķiršanas.nosaukumuSkats();
    }

    @Override
    public <T> KolonnaSkats<T> kolonnaSkats(Atribūts<T> atribūts) {
        return piešķiršanas.kolonnaSkats(atribūts);
    }

    @Override
    public ListView<Rinda> jēlaRindasSkats() {
        return piešķiršanas.jēlaRindasSkats();
    }

    @Override
    public void abonē_uz_iepriekšNoņemšana(PirmsNoņemšanasKlausītājs pirmsNoņemšanasKlausītājs) {
        primsNoņemšanaAbonēšanas.add(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public int izmērs() {
        return piešķiršanas.izmērs();
    }

    @Override
    public void noņemt(int rindasIndekss) {
        try {
            noņemt(piešķiršanas.jēlaRindasSkats().get(rindasIndekss));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void abonē_uz_pēcNoņemšana(PirmsNoņemšanasKlausītājs pirmsNoņemšanasKlausītājs) {
        pēcNoņemšanaAbonēšanas.add(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public Set<Rinda> piešķiršanas_no_piedāvājuma(Rinda piedāvājums) {
        final Set<Rinda> piešķiršanas_no_piedāvājuma = setOfUniques();
        try {
            lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu
                    .get(piedāvājums.indekss())
                    .forEach(piešķiršanasIndekss ->
                            piešķiršanas_no_piedāvājuma.add(piešķiršanas.jēlaRindasSkats().get(piešķiršanasIndekss)));
        } catch (RuntimeException e) {
            throw e;
        }
        return piešķiršanas_no_piedāvājuma;
    }

    @Override
    public Set<Rinda> piešķiršanas_no_prasības(Rinda prasība) {
        final Set<Rinda> piešķiršanas_no_prasības = setOfUniques();
        lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu
                .get(prasība.indekss())
                .forEach(piešķiršanasIndekss ->
                    piešķiršanas_no_prasības.add(piešķiršanas.jēlaRindasSkats().get(piešķiršanasIndekss)));
        return piešķiršanas_no_prasības;
    }

    @Override
    public List<Kolonna<Object>> kolonnaSkats() {
        return piešķiršanas.kolonnaSkats();
    }

    @Override
    public String toString() {
        return Piešķiršanas.class.getSimpleName() + path().toString();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        final net.splitcells.dem.data.set.list.List<String> path = list();
        path.addAll(prāsibas.path());
        path.add(vārds);
        return path;
    }

    @Override
    public Element toDom() {
        final var dom = element(Piešķiršanas.class.getSimpleName());
        dom.appendChild(textNode(path().toString()));
        jēlaRindasSkats().stream()
                .filter(rinda -> rinda != null)
                .forEach(rinda -> dom.appendChild(rinda.toDom()));
        return dom;
    }

    @Override
    public List<Rinda> jēlasRindas() {
        throw not_implemented_yet();
    }

    @Override
    public Rinda uzmeklēVienādus(Atribūts<Rinda> atribūts, Rinda cits) {
        return piešķiršanas.uzmeklēVienādus(atribūts, cits);
    }
}
