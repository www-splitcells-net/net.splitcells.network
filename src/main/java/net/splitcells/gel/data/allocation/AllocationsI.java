package net.splitcells.gel.data.allocation;

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
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Element;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.DatabaseI;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;

public class AllocationsI implements Allocations {
    protected final String vārds;
    protected final Database piešķiršanas;

    protected final List<AfterAdditionSubscriber> papildinājumsKlausītājs = list();
    protected final List<BeforeRemovalSubscriber> primsNoņemšanaAbonēšanas = list();
    protected final List<BeforeRemovalSubscriber> pēcNoņemšanaAbonēšanas = list();

    protected final Database piedāvājumi;
    protected final Database piedāvājumi_lietoti;
    protected final Database piedāvājumi_nelietoti;

    protected final Database prāsibas;
    protected final Database prāsibas_lietoti;
    protected final Database prāsibas_nelietoti;

    protected final Map<Integer, Integer> piešķiršanasIndekss_uz_lietotuPrāsibuIndekss = map();
    protected final Map<Integer, Integer> piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss = map();

    protected final Map<Integer, Set<Integer>> lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu = map();
    protected final Map<Integer, Set<Integer>> lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu = map();

    protected final Map<Integer, Set<Integer>> lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu = map();
    protected final Map<Integer, Set<Integer>> lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu = map();

    @Deprecated
    protected AllocationsI(String vārds, Database prasības, Database piedāvājumi) {
        this.vārds = vārds;
        piešķiršanas = new DatabaseI("piešķiršanas", this, concat(prasības.headerView(), piedāvājumi.headerView()));
        // DARĪT Noņemiet kodu un komentāru dublēšanos.
        {
            this.prāsibas = prasības;
            prāsibas_nelietoti = new DatabaseI("prasības_nelietoti", this, prasības.headerView());
            prāsibas_lietoti = new DatabaseI("prasības_lietoti", this, prasības.headerView());
            prasības.rawLinesView().forEach(prāsibas_nelietoti::add);
            prasības.subscribe_to_afterAddtions(prāsibas_nelietoti::add);
            prasības.subscriber_to_beforeRemoval(removalOf -> {
                if (lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.containsKey(removalOf.index())) {
                    listWithValuesOf(
                            lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(removalOf.index()))
                            .forEach(allocation_of_demand -> remove(piešķiršanas.rawLinesView().get(allocation_of_demand)));
                }
                if (prāsibas_nelietoti.contains(removalOf)) {
                    prāsibas_nelietoti.remove(removalOf);
                }
                // SALABOT Vai alternatīvā gadījumā būtu jādara kaut kas cits.
                if (prāsibas_lietoti.contains(removalOf)) {
                    prāsibas_lietoti.remove(removalOf);
                }
            });
        }
        {
            this.piedāvājumi = requireNonNull(piedāvājumi);
            piedāvājumi_nelietoti = new DatabaseI("piedāvājumi_nelietoti", this, piedāvājumi.headerView());
            piedāvājumi_lietoti = new DatabaseI("piedāvājumi_lietoti", this, piedāvājumi.headerView());
            piedāvājumi.rawLinesView().forEach(piedāvājumi_nelietoti::add);
            piedāvājumi.subscribe_to_afterAddtions(i -> {
                piedāvājumi_nelietoti.add(i);
            });
            piedāvājumi.subscriber_to_beforeRemoval(noņemšanaNo -> {
                if (lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.containsKey(noņemšanaNo.index())) {
                    listWithValuesOf
                            (lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(noņemšanaNo.index()))
                            .forEach(piešķiršanas_no_piedāvāijumu
                                    -> remove(piešķiršanas.rawLinesView().get(piešķiršanas_no_piedāvāijumu)));
                }
                if (piedāvājumi_nelietoti.contains(noņemšanaNo)) {
                    piedāvājumi_nelietoti.remove(noņemšanaNo);
                }
                // SALABOT Vai alternatīvā gadījumā būtu jādara kaut kas cits.
                if (piedāvājumi_lietoti.contains(noņemšanaNo)) {
                    piedāvājumi_lietoti.remove(noņemšanaNo);
                }
            });
        }
    }

    @Override
    public Database supplies() {
        return piedāvājumi;
    }

    @Override
    public Database supplies_used() {
        return piedāvājumi_lietoti;
    }

    @Override
    public Database supplies_unused() {
        return piedāvājumi_nelietoti;
    }

    @Override
    public Database demands() {
        return prāsibas;
    }

    @Override
    public Database demands_used() {
        return prāsibas_lietoti;
    }

    @Override
    public Database demands_unused() {
        return prāsibas_nelietoti;
    }

    @Override
    public Line allocate(Line prasība, Line piedāvājums) {
        final var piešķiršana = piešķiršanas.addTranslated(Line.saķēdet(prasība, piedāvājums));
        if (!lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.containsKey(piedāvājums.index())) {
            piedāvājumi_lietoti.add(piedāvājums);
            piedāvājumi_nelietoti.remove(piedāvājums);
        }
        if (!lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.containsKey(prasība.index())) {
            prāsibas_lietoti.add(prasība);
            prāsibas_nelietoti.remove(prasība);
        }
        {
            piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.put(piešķiršana.index(), prasība.index());
            piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.put(piešķiršana.index(), piedāvājums.index());
        }
        {
            {
                if (!lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.containsKey(prasība.index())) {
                    lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.put(prasība.index(), setOfUniques());
                }
                lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(prasība.index()).add(piešķiršana.index());
                if (!lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.containsKey(piedāvājums.index())) {
                    lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.put(piedāvājums.index(), setOfUniques());
                }
                lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(piedāvājums.index()).add(piešķiršana.index());
            }
        }
        {
            {
                if (!lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.containsKey(prasība.index())) {
                    lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.put(prasība.index(), setOfUniques());
                }
                lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.get(prasība.index()).add(piedāvājums.index());
            }
            {
                if (!lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.containsKey(piedāvājums.index())) {
                    lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.put(piedāvājums.index(), setOfUniques());
                }
                lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.get(piedāvājums.index()).add(prasība.index());
            }
        }
        papildinājumsKlausītājs.forEach(listener -> listener.reģistrē_papildinājumi(piešķiršana));
        return piešķiršana;
    }

    @Override
    public Line demand_of_allocation(Line piešķiršana) {
        return prāsibas.rawLinesView()
                .get(piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.get(piešķiršana.index()));
    }

    @Override
    public Line supply_of_allocation(Line allocation) {
        return piedāvājumi.rawLinesView()
                .get(piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.get(allocation.index()));
    }

    @Override
    public Line addTranslated(List<?> vertības) {
        throw not_implemented_yet();
    }

    @Override
    public Line add(Line rinda) {
        throw not_implemented_yet();
    }

    @Override
    public void remove(Line piešķiršana) {
        final var prasība = demand_of_allocation(piešķiršana);
        final var piedāvājums = supply_of_allocation(piešķiršana);
        primsNoņemšanaAbonēšanas.forEach(pirmsNoņemšanasKlausītājs -> pirmsNoņemšanasKlausītājs.rēgistrē_pirms_noņemšanas(piešķiršana));
        piešķiršanas.remove(piešķiršana);
        // TODO Make following code a remove subscription to allocations.
        {
            piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.remove(piešķiršana.index());
            piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.remove(piešķiršana.index());
        }
        {
            {
                lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.get(prasība.index()).remove(piedāvājums.index());
                if (lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.get(prasība.index()).isEmpty()) {
                    lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.remove(prasība.index());
                }
                lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.get(piedāvājums.index()).remove(prasība.index());
                if (lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.get(piedāvājums.index()).isEmpty()) {
                    lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.remove(piedāvājums.index());
                }
            }
            {
                lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(piedāvājums.index()).remove(piešķiršana.index());
                if (lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.get(piedāvājums.index()).isEmpty()) {
                    lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu.remove(piedāvājums.index());
                }
                lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(prasība.index()).remove(piešķiršana.index());
                if (lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.get(prasība.index()).isEmpty()) {
                    lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu.remove(prasība.index());
                }
            }
        }
        piešķiršanasIndekss_uz_lietotuPrāsibuIndekss.remove(piešķiršana.index());
        piešķiršanasIndekss_uz_lietotuPiedāvājumuIndekss.remove(piešķiršana.index());
        if (!lietotasPrāsibuIndekss_uz_lietotuPiedāvājumuIndekssu.containsKey(prasība.index())) {
            prāsibas_lietoti.remove(prasība);
            prāsibas_nelietoti.add(prasība);
        }
        if (!lietotasPiedāvājumuIndekss_uz_lietotuPrāsibuIndekssu.containsKey(piedāvājums.index())) {
            piedāvājumi_lietoti.remove(piedāvājums);
            piedāvājumi_nelietoti.add(piedāvājums);
        }
        pēcNoņemšanaAbonēšanas.forEach(listener -> listener.rēgistrē_pirms_noņemšanas(piešķiršana));
    }

    @Override
    public void subscribe_to_afterAddtions(AfterAdditionSubscriber klausītājs) {
        papildinājumsKlausītājs.add(klausītājs);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return piešķiršanas.headerView();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> atribūts) {
        return piešķiršanas.columnView(atribūts);
    }

    @Override
    public ListView<Line> rawLinesView() {
        return piešķiršanas.rawLinesView();
    }

    @Override
    public void subscriber_to_beforeRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        primsNoņemšanaAbonēšanas.add(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public int size() {
        return piešķiršanas.size();
    }

    @Override
    public void remove(int rindasIndekss) {
        try {
            remove(piešķiršanas.rawLinesView().get(rindasIndekss));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void subscriber_to_afterRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        pēcNoņemšanaAbonēšanas.add(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public Set<Line> allocations_of_supply(Line piedāvājums) {
        final Set<Line> piešķiršanas_no_piedāvājuma = setOfUniques();
        try {
            lietotasPiedāvājumuIndekss_uz_piešķiršanasIndekssu
                    .get(piedāvājums.index())
                    .forEach(piešķiršanasIndekss ->
                            piešķiršanas_no_piedāvājuma.add(piešķiršanas.rawLinesView().get(piešķiršanasIndekss)));
        } catch (RuntimeException e) {
            throw e;
        }
        return piešķiršanas_no_piedāvājuma;
    }

    @Override
    public Set<Line> allocations_of_demand(Line prasība) {
        final Set<Line> piešķiršanas_no_prasības = setOfUniques();
        lietotasPrāsibasIndekss_uz_piešķiršanasIndekssu
                .get(prasība.index())
                .forEach(piešķiršanasIndekss ->
                    piešķiršanas_no_prasības.add(piešķiršanas.rawLinesView().get(piešķiršanasIndekss)));
        return piešķiršanas_no_prasības;
    }

    @Override
    public List<Column<Object>> columnsView() {
        return piešķiršanas.columnsView();
    }

    @Override
    public String toString() {
        return Allocations.class.getSimpleName() + path().toString();
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
        final var dom = element(Allocations.class.getSimpleName());
        dom.appendChild(textNode(path().toString()));
        rawLinesView().stream()
                .filter(rinda -> rinda != null)
                .forEach(rinda -> dom.appendChild(rinda.toDom()));
        return dom;
    }

    @Override
    public List<Line> rawLines() {
        throw not_implemented_yet();
    }

    @Override
    public Line lookupEquals(Attribute<Line> atribūts, Line cits) {
        return piešķiršanas.lookupEquals(atribūts, cits);
    }
}
