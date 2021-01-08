package net.splitcells.gel.problēma;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.dati.tabula.Rinda;
import net.splitcells.gel.dati.tabula.kolonna.Kolonna;
import net.splitcells.gel.dati.tabula.kolonna.KolonnaSkats;
import net.splitcells.gel.atrisinājums.Atrisinājumi;
import net.splitcells.gel.dati.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.dati.datubāze.DatuBāze;
import net.splitcells.gel.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.ierobežojums.Ierobežojums;
import net.splitcells.gel.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.problēma.atvasināts.AtvasinātsAtrisinājums;
import net.splitcells.gel.novērtējums.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.atrisinājums.Atrisinājums;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.problēma.atvasināts.AtvasinātsAtrisinājums.atvasinātaProblema;

public class ProblēmaI implements Problēma {

    private final Ierobežojums ierobežojums;
    private final Piešķiršanas piešķiršanas;
    protected Atrisinājums kāAtrisinājums;

    public static Problēma problēma(Piešķiršanas piešķiršanas, Ierobežojums ierobežojums) {
        return new ProblēmaI(piešķiršanas, ierobežojums);
    }

    protected ProblēmaI(Piešķiršanas piešķiršanas, Ierobežojums ierobežojums) {
        this.piešķiršanas = piešķiršanas;
        this.ierobežojums = ierobežojums;
        sinhronizē(ierobežojums);
    }

    @Override
    public Ierobežojums ierobežojums() {
        return ierobežojums;
    }

    @Override
    public Piešķiršanas piešķiršanas() {
        return piešķiršanas;
    }

    @Override
    public Atrisinājums uzAtrisinājumu() {
        throw not_implemented_yet();
    }

    @Override
    public Atrisinājums kāAtrisinājums() {
        if (kāAtrisinājums == null) {
            kāAtrisinājums = Atrisinājumi.atrisinājum(this);
        }
        return kāAtrisinājums;
    }

    @Override
    public AtvasinātsAtrisinājums atvasinājums(Function<RefleksijaNovērtējums, RefleksijaNovērtējums> konversija) {
        return atvasinātaProblema(() -> list(), piešķiršanas, ierobežojums, konversija);
    }

    @Override
    public DatuBāze piedāvājums() {
        return this.piešķiršanas.piedāvājums();
    }

    @Override
    public DatuBāze piedāvājumi_lietoti() {
        return this.piešķiršanas.piedāvājumi_lietoti();
    }

    @Override
    public DatuBāze piedāvājums_nelietots() {
        return this.piešķiršanas.piedāvājums_nelietots();
    }

    @Override
    public DatuBāze prasība() {
        return this.piešķiršanas.prasība();
    }

    @Override
    public DatuBāze prasība_lietots() {
        return this.piešķiršanas.prasība_lietots();
    }

    @Override
    public DatuBāze prasības_nelietotas() {
        return this.piešķiršanas.prasības_nelietotas();
    }

    @Override
    public Rinda piešķirt(final Rinda prasība, final Rinda piedāvājums) {
        return this.piešķiršanas.piešķirt(prasība, piedāvājums);
    }

    @Override
    public Rinda prasība_no_piešķiršana(final Rinda piešķiršana) {
        return this.piešķiršanas.prasība_no_piešķiršana(piešķiršana);
    }

    @Override
    public Rinda piedāvājums_no_piešķiršana(final Rinda piešķiršana) {
        return this.piešķiršanas.piedāvājums_no_piešķiršana(piešķiršana);
    }

    @Override
    public java.util.Set<Rinda> piešķiršanas_no_piedāvājuma(final Rinda piedāvājuma) {
        return this.piešķiršanas.piešķiršanas_no_piedāvājuma(piedāvājuma);
    }

    @Override
    public java.util.Set<Rinda> piešķiršanasNo(final Rinda prasība, final Rinda piedāvājums) {
        return this.piešķiršanas.piešķiršanasNo(prasība, piedāvājums);
    }

    @Override
    public java.util.Set<Rinda> piešķiršanas_no_prasības(final Rinda prasība) {
        return this.piešķiršanas.piešķiršanas_no_prasības(prasība);
    }

    @Override
    public java.util.Set<Rinda> peidāvājumi_no_prasībam(final Rinda prasība) {
        return this.piešķiršanas.peidāvājumi_no_prasībam(prasība);
    }

    @Override
    public Rinda pieliktUnPārtulkot(List<?> vērtība) {
        return this.piešķiršanas.pieliktUnPārtulkot(vērtība);
    }

    @Override
    public Rinda pielikt(final Rinda rinda) {
        return this.piešķiršanas.pielikt(rinda);
    }

    @Override
    public void noņemt(final int rindasIndekss) {
        this.piešķiršanas.noņemt(rindasIndekss);
    }

    @Override
    public void noņemt(final Rinda rinda) {
        this.piešķiršanas.noņemt(rinda);
    }

    @Override
    public void aizvietot(final Rinda newRinda) {
        this.piešķiršanas.aizvietot(newRinda);
    }

    @Override
    public <T extends PapildinājumsKlausītājs & PirmsNoņemšanasKlausītājs> void sinhronizē(final T klausītājs) {
        this.piešķiršanas.<T>sinhronizē(klausītājs);
    }

    @Override
    public void abonē_uz_papildinājums(final PapildinājumsKlausītājs klausītājs) {
        this.piešķiršanas.abonē_uz_papildinājums(klausītājs);
    }

    @Override
    public void abonē_uz_iepriekšNoņemšana(final PirmsNoņemšanasKlausītājs pirmsNoņemšanasKlausītājs) {
        this.piešķiršanas.abonē_uz_iepriekšNoņemšana(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public void abonē_uz_pēcNoņemšana(final PirmsNoņemšanasKlausītājs klausītājs) {
        this.piešķiršanas.abonē_uz_pēcNoņemšana(klausītājs);
    }

    @Override
    public List<Atribūts<Object>> nosaukumuSkats() {
        return this.piešķiršanas.nosaukumuSkats();
    }

    @Override
    public <T extends Object> KolonnaSkats<T> kolonnaSkats(final Atribūts<T> atribūts) {
        return this.piešķiršanas.<T>kolonnaSkats(atribūts);
    }

    @Override
    public List<Kolonna<Object>> kolonnaSkats() {
        return this.piešķiršanas.kolonnaSkats();
    }

    @Deprecated
    public ListView<Rinda> jēlaRindasSkats() {
        return this.piešķiršanas.jēlaRindasSkats();
    }

    @Override
    public boolean satur(final Rinda rinda) {
        return this.piešķiršanas.satur(rinda);
    }

    @Override
    public List<Rinda> gūtRindas() {
        return this.piešķiršanas.gūtRindas();
    }

    @Override
    public Rinda gūtJēluRindas(final int indekss) {
        return this.piešķiršanas.gūtJēluRindas(indekss);
    }

    @Override
    public int izmērs() {
        return this.piešķiršanas.izmērs();
    }

    @Override
    public boolean irTukšs() {
        return this.piešķiršanas.irTukšs();
    }

    @Override
    public boolean navTukšs() {
        return this.piešķiršanas.navTukšs();
    }

    @Override
    public List<Rinda> jēlasRindas() {
        return this.piešķiršanas.jēlasRindas();
    }

    public String uzCSV() {
        return this.piešķiršanas.uzCSV();
    }

    @Override
    public Rinda uzmeklēVienādus(final Atribūts<Rinda> atribūts, final Rinda other) {
        return this.piešķiršanas.uzmeklēVienādus(atribūts, other);
    }

    @Override
    public org.w3c.dom.Element uzFods() {
        return this.piešķiršanas.uzFods();
    }

    @Override
    public List<String> path() {
        return this.piešķiršanas.path();
    }

    public org.w3c.dom.Node toDom() {
        return this.piešķiršanas.toDom();
    }
}
