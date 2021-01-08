package net.splitcells.gel.solution.history;

import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.StreamUtils.reverse;
import static net.splitcells.gel.Language.*;
import static net.splitcells.gel.data.datubāze.DatuBāzes.datuBāze;
import static net.splitcells.gel.solution.history.refleksija.tips.PiešķiršanaNovērtējums.pieškiršanasNovērtejums;
import static net.splitcells.gel.solution.history.refleksija.tips.PilnsNovērtejums.pilnsNovērtejums;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.notikums.Piešķiršana;
import net.splitcells.gel.solution.history.notikums.PiešķiršanaMaiņaTips;
import net.splitcells.gel.solution.history.refleksija.RefleksijasDatiI;
import net.splitcells.gel.data.tabula.kolonna.Kolonna;
import net.splitcells.gel.data.tabula.kolonna.KolonnaSkats;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.data.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.data.datubāze.DatuBāze;
import net.splitcells.gel.data.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.data.piešķiršanas.Piešķiršanass;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.solution.history.refleksija.tips.PiešķiršanaNovērtējums;
import net.splitcells.gel.solution.history.refleksija.tips.PilnsNovērtejums;
import org.w3c.dom.Node;

import java.util.Set;

public class VēstureI implements Vēsture {

    private final Solution solution;
    private int pēdējaNotikumuId = -1;
    private Piešķiršanas piešķiršanas;

    protected VēstureI(Solution solution) {
        piešķiršanas = Piešķiršanass.piešķiršanas
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
        final var refleksijasDati = RefleksijasDatiI.refleksijasDatī();
        refleksijasDati.ar(PilnsNovērtejums.class
                , pilnsNovērtejums(solution.ierobežojums().novērtējums()));
        refleksijasDati.ar(PiešķiršanaNovērtējums.class
                , pieškiršanasNovērtejums(solution.ierobežojums().novērtējums(piešķiršanasVertība)));
        final Rinda piešķiršana
                = prasība().pieliktUnPārtulkot(list(
                parceltPedeijuNotikumuIdUzpriekšu()
                , Piešķiršana.piešķiršana(PiešķiršanaMaiņaTips.PAPILDINĀJUMS
                        , solution.prasība_no_piešķiršana(piešķiršanasVertība)
                        , solution.piedāvājums_no_piešķiršana(piešķiršanasVertība))));
        piešķiršanas.piešķirt(piešķiršana, this.piedāvājums().pieliktUnPārtulkot(list(refleksijasDati)));
    }

    @Override
    public void rēgistrē_pirms_noņemšanas(Rinda noņemtAtrisinājums) {
        final var refleksijasDati = RefleksijasDatiI.refleksijasDatī();
        refleksijasDati.ar(PilnsNovērtejums.class
                , pilnsNovērtejums(solution.ierobežojums().novērtējums()));
        refleksijasDati.ar(PiešķiršanaNovērtējums.class
                , pieškiršanasNovērtejums(solution.ierobežojums().novērtējums(noņemtAtrisinājums)));
        final Rinda pieķiršanas
                = prasība().pieliktUnPārtulkot(list(
                parceltPedeijuNotikumuIdUzpriekšu()
                , Piešķiršana.piešķiršana(PiešķiršanaMaiņaTips.NOŅEMŠANA
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
        if (notikumuTips.equals(PiešķiršanaMaiņaTips.PAPILDINĀJUMS)) {
            final var pieškiršanas = solution.piešķiršanasNo
                    (notikumuKoNoņemnt.demand().uzRindaRādītājs().interpretē(solution.prasība()).get()
                            , notikumuKoNoņemnt.supply().uzRindaRādītājs().interpretē(solution.piedāvājums()).get());
            assertThat(pieškiršanas).hasSize(1);
            pieškiršanas.forEach(e -> solution.noņemt(e));
        } else if (notikumuTips.equals(PiešķiršanaMaiņaTips.NOŅEMŠANA)) {
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
    public void abonē_uz_papildinājums(PapildinājumsKlausītājs klausītājs) {
        piešķiršanas.abonē_uz_papildinājums(klausītājs);
    }

    @Override
    public void abonē_uz_iepriekšNoņemšana(PirmsNoņemšanasKlausītājs pirmsNoņemšanasKlausītājs) {
        piešķiršanas.abonē_uz_iepriekšNoņemšana(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public void abonē_uz_pēcNoņemšana(PirmsNoņemšanasKlausītājs pirmsNoņemšanasKlausītājs) {
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
    public DatuBāze piedāvājums() {
        return piešķiršanas.piedāvājums();
    }

    @Override
    public DatuBāze piedāvājumi_lietoti() {
        return piešķiršanas.piedāvājumi_lietoti();
    }

    @Override
    public DatuBāze piedāvājums_nelietots() {
        return piešķiršanas.piedāvājums_nelietots();
    }

    @Override
    public DatuBāze prasība() {
        return piešķiršanas.prasība();
    }

    @Override
    public DatuBāze prasība_lietots() {
        return piešķiršanas.prasība_lietots();
    }

    @Override
    public DatuBāze prasības_nelietotas() {
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
