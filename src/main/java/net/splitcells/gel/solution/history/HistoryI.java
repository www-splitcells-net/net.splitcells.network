package net.splitcells.gel.solution.history;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.StreamUtils.reverse;
import static net.splitcells.gel.Language.*;
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
import net.splitcells.gel.data.table.kolonna.Kolonna;
import net.splitcells.gel.data.table.kolonna.KolonnaSkats;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.allocation.Allocationss;
import net.splitcells.gel.data.table.Rinda;
import net.splitcells.gel.data.table.atribūts.Atribūts;
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
                (VĒSTURE.apraksts()
                        , datuBāze
                                (NOTIKUMS.apraksts()
                                        , () -> solution.path().withAppended(VĒSTURE.apraksts())
                                        , PIEŠĶIRŠANA_ID, PIEŠĶIRŠANAS_NOTIKUMS)
                        , datuBāze
                                (RADĪJUMS.apraksts()
                                        , () -> solution.path().withAppended(VĒSTURE.apraksts())
                                        , REFLEKSIJAS_DATI));
        this.solution = solution;
        solution.abonē_uz_papildinājums(this);
        solution.abonē_uz_iepriekšNoņemšana(this);
    }

    @Override
    public void reģistrē_papildinājumi(Rinda piešķiršanasVertība) {
        final var refleksijasDati = MetaDataI.refleksijasDatī();
        refleksijasDati.ar(CompleteRating.class
                , pilnsNovērtejums(solution.ierobežojums().novērtējums()));
        refleksijasDati.ar(AllocationRating.class
                , pieškiršanasNovērtejums(solution.ierobežojums().novērtējums(piešķiršanasVertība)));
        final Rinda piešķiršana
                = prasība().pieliktUnPārtulkot(list(
                parceltPedeijuNotikumuIdUzpriekšu()
                , Allocation.piešķiršana(AllocationChangeType.PAPILDINĀJUMS
                        , solution.prasība_no_piešķiršana(piešķiršanasVertība)
                        , solution.piedāvājums_no_piešķiršana(piešķiršanasVertība))));
        piešķiršanas.piešķirt(piešķiršana, this.piedāvājums().pieliktUnPārtulkot(list(refleksijasDati)));
    }

    @Override
    public void rēgistrē_pirms_noņemšanas(Rinda noņemtAtrisinājums) {
        final var refleksijasDati = MetaDataI.refleksijasDatī();
        refleksijasDati.ar(CompleteRating.class
                , pilnsNovērtejums(solution.ierobežojums().novērtējums()));
        refleksijasDati.ar(AllocationRating.class
                , pieškiršanasNovērtejums(solution.ierobežojums().novērtējums(noņemtAtrisinājums)));
        final Rinda pieķiršanas
                = prasība().pieliktUnPārtulkot(list(
                parceltPedeijuNotikumuIdUzpriekšu()
                , Allocation.piešķiršana(AllocationChangeType.NOŅEMŠANA
                        , solution.prasība_no_piešķiršana(noņemtAtrisinājums)
                        , solution.piedāvājums_no_piešķiršana(noņemtAtrisinājums))));
        piešķiršanas.piešķirt(pieķiršanas, this.piedāvājums().pieliktUnPārtulkot(list(refleksijasDati)));
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
                (rangeClosed(indekss, this.izmērs() - 1)
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
        final var indekss = izmērs() - 1;
        final var notikumuKoNoņemnt = kolonnaSkats(PIEŠĶIRŠANA_ID)
                .uzmeklēšana(indekss)
                .gūtRinda(0)
                .vērtība(PIEŠĶIRŠANAS_NOTIKUMS);
        final var notikumuTips = notikumuKoNoņemnt.tips();
        if (notikumuTips.equals(AllocationChangeType.PAPILDINĀJUMS)) {
            final var pieškiršanas = solution.piešķiršanasNo
                    (notikumuKoNoņemnt.demand().uzRindaRādītājs().interpretē(solution.prasība()).get()
                            , notikumuKoNoņemnt.supply().uzRindaRādītājs().interpretē(solution.piedāvājums()).get());
            assertThat(pieškiršanas).hasSize(1);
            pieškiršanas.forEach(e -> solution.noņemt(e));
        } else if (notikumuTips.equals(AllocationChangeType.NOŅEMŠANA)) {
            solution.piešķirt
                    (notikumuKoNoņemnt.demand().uzRindaRādītājs().interpretē(solution.prasība()).get()
                            , notikumuKoNoņemnt.supply().uzRindaRādītājs().interpretē(solution.piedāvājums()).get());
        } else {
            throw new UnsupportedOperationException();
        }
        atgrieztPedeijo_noņemt(indekss);
    }

    protected void atgrieztPedeijo_noņemt(int indekss) {
        noņemt_(kolonnaSkats(PIEŠĶIRŠANA_ID).uzmeklēšana(indekss + 1).gūtRinda(0));
        noņemt_(kolonnaSkats(PIEŠĶIRŠANA_ID).uzmeklēšana(indekss).gūtRinda(0));
    }

    protected void noņemt_(Rinda rinda) {
        piešķiršanas.noņemt(rinda);
        --pēdējaNotikumuId;
    }

    @Override
    public void noņemt(Rinda rinda) {
        throw not_implemented_yet();
    }

    @Override
    public void abonē_uz_papildinājums(AfterAdditionSubscriber klausītājs) {
        piešķiršanas.abonē_uz_papildinājums(klausītājs);
    }

    @Override
    public void abonē_uz_iepriekšNoņemšana(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        piešķiršanas.abonē_uz_iepriekšNoņemšana(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public void abonē_uz_pēcNoņemšana(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        piešķiršanas.abonē_uz_pēcNoņemšana(pirmsNoņemšanasKlausītājs);
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
    public void noņemt(int indekss) {
        if (izmērs() != indekss + 1) {
            throw not_implemented_yet();
        }
        piešķiršanas.noņemt(jēlaRindasSkats().get(indekss));
    }

    @Override
    public int momentansIndekss() {
        return pēdējaNotikumuId;
    }

    @Override
    public Rinda piešķirt(Rinda prasība, Rinda piedāvājums) {
        throw not_implemented_yet();
    }

    @Override
    public Database piedāvājums() {
        return piešķiršanas.piedāvājums();
    }

    @Override
    public Database piedāvājumi_lietoti() {
        return piešķiršanas.piedāvājumi_lietoti();
    }

    @Override
    public Database piedāvājums_nelietots() {
        return piešķiršanas.piedāvājums_nelietots();
    }

    @Override
    public Database prasība() {
        return piešķiršanas.prasība();
    }

    @Override
    public Database prasība_lietots() {
        return piešķiršanas.prasība_lietots();
    }

    @Override
    public Database prasības_nelietotas() {
        return piešķiršanas.prasības_nelietotas();
    }

    @Override
    public Rinda prasība_no_piešķiršana(Rinda piešķiršana) {
        return piešķiršanas.prasība_no_piešķiršana(piešķiršana);
    }

    @Override
    public Rinda piedāvājums_no_piešķiršana(Rinda piešķiršana) {
        return piešķiršanas.piedāvājums_no_piešķiršana(piešķiršana);
    }

    @Override
    public Set<Rinda> piešķiršanas_no_piedāvājuma(Rinda piedāvājums) {
        return piešķiršanas.piešķiršanas_no_piedāvājuma(piedāvājums);
    }

    @Override
    public Set<Rinda> piešķiršanas_no_prasības(Rinda prasība) {
        return piešķiršanas.piešķiršanas_no_prasības(prasība);
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
    public List<Kolonna<Object>> kolonnaSkats() {
        return piešķiršanas.kolonnaSkats();
    }

    @Override
    public ListView<Rinda> jēlaRindasSkats() {
        return piešķiršanas.jēlaRindasSkats();
    }

    @Override
    public int izmērs() {
        return piešķiršanas.izmērs();
    }

    @Override
    public List<Rinda> jēlasRindas() {
        return piešķiršanas.jēlasRindas();
    }

    @Override
    public Rinda uzmeklēVienādus(Atribūts<Rinda> atribūts, Rinda cits) {
        return piešķiršanas.uzmeklēVienādus(atribūts, cits);
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
