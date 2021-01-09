package net.splitcells.gel.solution.history;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.StreamUtils.reverse;
import static net.splitcells.gel.common.Language.*;
import static net.splitcells.gel.data.database.Databases.datuBāze;
import static net.splitcells.gel.solution.history.meta.type.AllocationRating.pieškiršanasNovērtejums;
import static net.splitcells.gel.solution.history.meta.type.CompleteRating.pilnsNovērtejums;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.solution.history.event.AllocationChangeType;
import net.splitcells.gel.solution.history.meta.MetaDataI;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.allocation.Allocationss;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.type.AllocationRating;
import net.splitcells.gel.solution.history.meta.type.CompleteRating;
import org.w3c.dom.Node;

import java.util.Set;

public class HistoryI implements History {

    private final Solution solution;
    private int pēdējaNotikumuId = -1;
    private Allocations piešķiršanas;

    protected HistoryI(Solution solution) {
        piešķiršanas = Allocationss.piešķiršanas
                (HISTORY.value()
                        , datuBāze
                                (EVENT.value()
                                        , () -> solution.path().withAppended(HISTORY.value())
                                        , PIEŠĶIRŠANA_ID, PIEŠĶIRŠANAS_NOTIKUMS)
                        , datuBāze
                                (RESULT.value()
                                        , () -> solution.path().withAppended(HISTORY.value())
                                        , REFLEKSIJAS_DATI));
        this.solution = solution;
        solution.subscribe_to_afterAddtions(this);
        solution.subscriber_to_beforeRemoval(this);
    }

    @Override
    public void reģistrē_papildinājumi(Line piešķiršanasVertība) {
        final var refleksijasDati = MetaDataI.refleksijasDatī();
        refleksijasDati.ar(CompleteRating.class
                , pilnsNovērtejums(solution.constraint().rating()));
        refleksijasDati.ar(AllocationRating.class
                , pieškiršanasNovērtejums(solution.constraint().novērtējums(piešķiršanasVertība)));
        final Line piešķiršana
                = demands().addTranslated(list(
                parceltPedeijuNotikumuIdUzpriekšu()
                , Allocation.piešķiršana(AllocationChangeType.PAPILDINĀJUMS
                        , solution.demand_of_allocation(piešķiršanasVertība)
                        , solution.supply_of_allocation(piešķiršanasVertība))));
        piešķiršanas.allocate(piešķiršana, this.supplies().addTranslated(list(refleksijasDati)));
    }

    @Override
    public void rēgistrē_pirms_noņemšanas(Line noņemtAtrisinājums) {
        final var refleksijasDati = MetaDataI.refleksijasDatī();
        refleksijasDati.ar(CompleteRating.class
                , pilnsNovērtejums(solution.constraint().rating()));
        refleksijasDati.ar(AllocationRating.class
                , pieškiršanasNovērtejums(solution.constraint().novērtējums(noņemtAtrisinājums)));
        final Line pieķiršanas
                = demands().addTranslated(list(
                parceltPedeijuNotikumuIdUzpriekšu()
                , Allocation.piešķiršana(AllocationChangeType.NOŅEMŠANA
                        , solution.demand_of_allocation(noņemtAtrisinājums)
                        , solution.supply_of_allocation(noņemtAtrisinājums))));
        piešķiršanas.allocate(pieķiršanas, this.supplies().addTranslated(list(refleksijasDati)));
    }

    protected Integer parceltPedeijuNotikumuIdAtpakal() {
        return pēdējaNotikumuId -= 1;
    }

    protected Integer parceltPedeijuNotikumuIdUzpriekšu() {
        return pēdējaNotikumuId += 1;
    }

    @Override
    public void atiestatUz(int indekss) {
        final var indeksiUzAtgrieztu = reverse
                (rangeClosed(indekss, this.size() - 1)
                        .boxed()
                        .filter(i -> i != -1)
                        .filter(i -> i != indekss)
                ).collect(Lists.toList());
        atiestatUz(indeksiUzAtgrieztu);
    }

    protected void atiestatUz(List<Integer> indeksi) {
        indeksi.forEach(i -> atgrieztPedeijo());
    }

    protected void atgrieztPedeijo() {
        final var indekss = size() - 1;
        final var notikumuKoNoņemnt = columnView(PIEŠĶIRŠANA_ID)
                .uzmeklēšana(indekss)
                .gūtRinda(0)
                .vērtība(PIEŠĶIRŠANAS_NOTIKUMS);
        final var notikumuTips = notikumuKoNoņemnt.tips();
        if (notikumuTips.equals(AllocationChangeType.PAPILDINĀJUMS)) {
            final var pieškiršanas = solution.allocationsOf
                    (notikumuKoNoņemnt.demand().uzRindaRādītājs().interpretē(solution.demands()).get()
                            , notikumuKoNoņemnt.supply().uzRindaRādītājs().interpretē(solution.supplies()).get());
            assertThat(pieškiršanas).hasSize(1);
            pieškiršanas.forEach(e -> solution.remove(e));
        } else if (notikumuTips.equals(AllocationChangeType.NOŅEMŠANA)) {
            solution.allocate
                    (notikumuKoNoņemnt.demand().uzRindaRādītājs().interpretē(solution.demands()).get()
                            , notikumuKoNoņemnt.supply().uzRindaRādītājs().interpretē(solution.supplies()).get());
        } else {
            throw new UnsupportedOperationException();
        }
        atgrieztPedeijo_noņemt(indekss);
    }

    protected void atgrieztPedeijo_noņemt(int indekss) {
        noņemt_(columnView(PIEŠĶIRŠANA_ID).uzmeklēšana(indekss + 1).gūtRinda(0));
        noņemt_(columnView(PIEŠĶIRŠANA_ID).uzmeklēšana(indekss).gūtRinda(0));
    }

    protected void noņemt_(Line rinda) {
        piešķiršanas.remove(rinda);
        --pēdējaNotikumuId;
    }

    @Override
    public void remove(Line rinda) {
        throw not_implemented_yet();
    }

    @Override
    public void subscribe_to_afterAddtions(AfterAdditionSubscriber klausītājs) {
        piešķiršanas.subscribe_to_afterAddtions(klausītājs);
    }

    @Override
    public void subscriber_to_beforeRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        piešķiršanas.subscriber_to_beforeRemoval(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public void subscriber_to_afterRemoval(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        piešķiršanas.subscriber_to_afterRemoval(pirmsNoņemšanasKlausītājs);
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
    public void remove(int indekss) {
        if (size() != indekss + 1) {
            throw not_implemented_yet();
        }
        piešķiršanas.remove(rawLinesView().get(indekss));
    }

    @Override
    public int momentansIndekss() {
        return pēdējaNotikumuId;
    }

    @Override
    public Line allocate(Line prasība, Line piedāvājums) {
        throw not_implemented_yet();
    }

    @Override
    public Database supplies() {
        return piešķiršanas.supplies();
    }

    @Override
    public Database supplies_used() {
        return piešķiršanas.supplies_used();
    }

    @Override
    public Database supplies_unused() {
        return piešķiršanas.supplies_unused();
    }

    @Override
    public Database demands() {
        return piešķiršanas.demands();
    }

    @Override
    public Database demands_used() {
        return piešķiršanas.demands_used();
    }

    @Override
    public Database demands_unused() {
        return piešķiršanas.demands_unused();
    }

    @Override
    public Line demand_of_allocation(Line piešķiršana) {
        return piešķiršanas.demand_of_allocation(piešķiršana);
    }

    @Override
    public Line supply_of_allocation(Line piešķiršana) {
        return piešķiršanas.supply_of_allocation(piešķiršana);
    }

    @Override
    public Set<Line> allocations_of_supply(Line piedāvājums) {
        return piešķiršanas.allocations_of_supply(piedāvājums);
    }

    @Override
    public Set<Line> allocations_of_demand(Line prasība) {
        return piešķiršanas.allocations_of_demand(prasība);
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
    public List<Column<Object>> columnsView() {
        return piešķiršanas.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return piešķiršanas.rawLinesView();
    }

    @Override
    public int size() {
        return piešķiršanas.size();
    }

    @Override
    public List<Line> rawLines() {
        return piešķiršanas.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> atribūts, Line cits) {
        return piešķiršanas.lookupEquals(atribūts, cits);
    }

    @Override
    public Node toDom() {
        throw not_implemented_yet();
    }

    @Override
    public List<String> path() {
        return piešķiršanas.path();
    }
}
