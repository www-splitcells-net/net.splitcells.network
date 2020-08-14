package net.splitcells.gel.kodols.problēma.atvasināts;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.kodols.dati.tabula.kolonna.Kolonna;
import net.splitcells.gel.kodols.dati.tabula.kolonna.KolonnaSkats;
import net.splitcells.gel.kodols.dati.datubāze.PapildinājumsKlausītājs;
import net.splitcells.gel.kodols.dati.datubāze.DatuBāze;
import net.splitcells.gel.kodols.dati.datubāze.PirmsNoņemšanasKlausītājs;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;
import net.splitcells.gel.kodols.atrisinājums.vēsture.Vēsture;
import net.splitcells.gel.kodols.atrisinājums.vēsture.Vēstures;
import org.w3c.dom.Node;

import java.util.Set;
import java.util.function.Function;

import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.gel.kodols.ierobežojums.tips.Atvasināšana.atvasināšana;


public class AtvasinātsAtrisinājums implements Atrisinājums {

    protected Ierobežojums ierobežojums;
    private final Piešķiršanas piešķiršanas;
    private final Vēsture vēsture;
    private final Discoverable konteksts;
    
    public static AtvasinātsAtrisinājums atvasinātaProblema(Discoverable konteksts, Piešķiršanas piešķiršanas, Ierobežojums originalIerobežojums, Function<RefleksijaNovērtējums, RefleksijaNovērtējums> atvasinātsFunkcija) {
        return new AtvasinātsAtrisinājums(konteksts, piešķiršanas, originalIerobežojums, atvasināšana(originalIerobežojums, atvasinātsFunkcija));
    }

    protected AtvasinātsAtrisinājums(Discoverable konteksts, Piešķiršanas piešķiršanas, Ierobežojums oriģinālaisIerobežojums, Function<RefleksijaNovērtējums, RefleksijaNovērtējums> atvasinātsFunkcija) {
        this(konteksts, piešķiršanas, oriģinālaisIerobežojums, atvasināšana(oriģinālaisIerobežojums, atvasinātsFunkcija));
    }

    public static AtvasinātsAtrisinājums atvasinātsProblēma(Discoverable konteksts, Piešķiršanas piešķiršanas, Ierobežojums ierobežojums, Ierobežojums atvasināšana) {
        return new AtvasinātsAtrisinājums(konteksts, piešķiršanas, ierobežojums, atvasināšana);
    }

    protected AtvasinātsAtrisinājums(Discoverable konteksts, Piešķiršanas piešķiršanas, Ierobežojums ierobežojums, Ierobežojums atvasināšana) {
        this.piešķiršanas = piešķiršanas;
        this.ierobežojums = atvasināšana;
        vēsture = Vēstures.vēsture(this);
        this.konteksts = konteksts;
    }

    protected AtvasinātsAtrisinājums(Discoverable konteksts, Piešķiršanas piešķiršanas) {
        this.piešķiršanas = piešķiršanas;
        vēsture = Vēstures.vēsture(this);
        this.konteksts = konteksts;
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
    public Atrisinājums uzProblēma() {
        throw not_implemented_yet();
    }

    @Override
    public Atrisinājums kāProblēma() {
        throw not_implemented_yet();
    }

    @Override
    public AtvasinātsAtrisinājums atvasinājums(Function<RefleksijaNovērtējums, RefleksijaNovērtējums> atvasināšana) {
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
    public Rinda piešķirt(Rinda prasība, Rinda piedāvājums) {
        return piešķiršanas.piešķirt(prasība, piedāvājums);
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
    public Rinda pieliktUnPārtulkot(List<?> vērtība) {
        return piešķiršanas.pieliktUnPārtulkot(vērtība);
    }

    @Override
    public Rinda pielikt(Rinda rinda) {
        return piešķiršanas.pielikt(rinda);
    }

    @Override
    public void noņemt(int lineIndex) {
        piešķiršanas.noņemt(lineIndex);
    }

    @Override
    public void noņemt(Rinda rinda) {
        piešķiršanas.noņemt(rinda);
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
    public void abonē_uy_pēcNoņemšana(PirmsNoņemšanasKlausītājs klausītājs) {
        piešķiršanas.abonē_uy_pēcNoņemšana(klausītājs);
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
    public Rinda uzmeklēVienādus(Atribūts<Rinda> atribūts, Rinda other) {
        return piešķiršanas.uzmeklēVienādus(atribūts, other);
    }

    @Override
    public Node toDom() {
        throw not_implemented_yet();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        return konteksts.path().withAppended(AtvasinātsAtrisinājums.class.getSimpleName());
    }

    @Override
    public Vēsture vēsture() {
        return vēsture;
    }
}
