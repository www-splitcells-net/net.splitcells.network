package net.splitcells.gel.dati.datubāze;

import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static net.splitcells.dem.utils.CommonFunctions.removeAny;
import static net.splitcells.dem.lang.Xml.*;
import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.ListViewI.listView;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.dati.tabula.Rinda;
import net.splitcells.gel.dati.tabula.RindaI;
import net.splitcells.gel.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.dati.tabula.kolonna.Kolonna;
import net.splitcells.gel.dati.tabula.kolonna.KolonnaI;
import net.splitcells.gel.dati.tabula.kolonna.KolonnaSkats;
import net.splitcells.gel.dati.tabula.kolonna.KolonnaSkatsI;
import org.w3c.dom.Element;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.dem.object.Discoverable;

public class DatuBāzeI implements DatuBāze {
    protected final String vārds;
    protected final Optional<Discoverable> vecāks;
    protected final List<Atribūts<Object>> atribūti;
    protected final List<Kolonna<Object>> kolonnas = list();
    protected final Map<Atribūts<?>, Integer> tips_kolonnasIndekss = map();
    protected final Set<Rinda> rindas = setOfUniques();
    protected final List<Rinda> jēlasRindas = list();
    protected final ListView<Rinda> jēlasRindasSkats = listView(jēlasRindas);
    protected int izmers;
    protected final List<PapildinājumsKlausītājs> papildinājumsKlausītājs = list();
    protected final List<PirmsNoņemšanasKlausītājs> pirmsNoņemšanaKlausītājs = list();
    protected final List<PirmsNoņemšanasKlausītājs> pēcNoņemšanaAbonēšanas = list();
    protected final net.splitcells.dem.data.set.Set<Integer> indekssiNelitoti = setOfUniques();


    @Deprecated
    public DatuBāzeI(List<Atribūts<? extends Object>> atribūti) {
        this("", null, atribūti.mapped(a -> (Atribūts<Object>) a));
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public DatuBāzeI(String vārds, Discoverable vecāks, List<Atribūts<Object>> atribūti) {
        this.vārds = vārds;
        this.vecāks = Optional.ofNullable(vecāks);
        final List<Atribūts<Object>> headerAtribūts = list();
        atribūti.forEach(att -> {
            tips_kolonnasIndekss.put(att, headerAtribūts.size());
            headerAtribūts.add(att);
            kolonnas.add(KolonnaI.kolonna(this, att));
        });
        this.atribūti = listWithValuesOf(headerAtribūts);
        kolonnas.forEach(this::abonē_uz_papildinājums);
        kolonnas.forEach(this::abonē_uz_iepriekšNoņemšana);
    }

    @Deprecated
    public DatuBāzeI(List<Atribūts<?>> atribūti, Collection<List<Object>> rindasVertības) {
        this(atribūti);
        rindasVertības.forEach(line_values -> pieliktUnPārtulkot(line_values));
    }

    public DatuBāzeI(String vārds, Discoverable vecāks, Atribūts<? extends Object>... atribūti) {
        this(vārds, vecāks, listWithValuesOf(atribūti).mapped(a -> (Atribūts<Object>) a));
    }

    @Deprecated
    public DatuBāzeI(Atribūts<?>... atribūti) {
        this(listWithValuesOf(atribūti));
    }

    @Override
    public List<Atribūts<Object>> nosaukumuSkats() {
        return atribūti;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> KolonnaSkats<T> kolonnaSkats(Atribūts<T> atribūts) {
        return KolonnaSkatsI.kolonnasSkats((Kolonna<T>) kolonnas.get(tips_kolonnasIndekss.get(atribūts)));
    }

    @Override
    public ListView<Rinda> jēlaRindasSkats() {
        return jēlasRindasSkats;
    }

    @Override
    public void abonē_uz_papildinājums(PapildinājumsKlausītājs klausītājs) {
        this.papildinājumsKlausītājs.add(klausītājs);
    }

    @Override
    public void abonē_uz_iepriekšNoņemšana(PirmsNoņemšanasKlausītājs pirmsNoņemšanasKlausītājs) {
        pirmsNoņemšanaKlausītājs.add(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public Rinda pielikt(Rinda rinda) {
        final List<Object> rindasVertības = list();
        range(0, atribūti.size()).forEach(i -> {
            rindasVertības.add(rinda.vērtība(atribūti.get(i)));
        });
        return pieliktTulkošanaNo(rindasVertības, rinda.indekss());
    }

    protected Rinda pieliktTulkošanaNo(List<Object> rindasVertības, int indekss) {
        if (indekss >= jēlasRindas.size()) {
            range(0, rindasVertības.size()).forEach(i -> {
                paplašināt_sarakstu_uz(kolonnas.get(i), indekss);
            });
            rangeClosed(jēlasRindas.size(), indekss).forEach(i -> {
                indekssiNelitoti.add(i);
                jēlasRindas.add(null);
            });
        }
        indekssiNelitoti.delete(indekss);
        range(0, rindasVertības.size()).forEach(i -> kolonnas.get(i).set(indekss, rindasVertības.get(i)));
        ++izmers;
        final var rinda = RindaI.rinda(this, indekss);
        jēlasRindas.set(rinda.indekss(), rinda);
        rindas.add(rinda);
        papildinājumsKlausītājs.forEach(klausītājs -> klausītājs.reģistrē_papildinājumi(rinda));
        return rinda;
    }

    /**
     * TODO Move this to an utility class.
     */
    protected static void paplašināt_sarakstu_uz(List<?> sarakst, int mērķetasMaksimalaisIndekss) {
        while (sarakst.size() < mērķetasMaksimalaisIndekss + 1) {
            sarakst.add(null);
        }
    }

    @Override
    public Rinda pieliktUnPārtulkot(List<? extends Object> rindasVertības) {
        final int rindasIndekss;
        final Rinda rinda;
        if (indekssiNelitoti.isEmpty()) {
            rindasIndekss = jēlasRindas.size();
            rinda = RindaI.rinda(this, rindasIndekss);
            jēlasRindas.add(rinda);
            range(0, rindasVertības.size()).forEach(i -> kolonnas.get(i).add(rindasVertības.get(i)));
        } else {
            rindasIndekss = removeAny(indekssiNelitoti);
            range(0, rindasVertības.size()).forEach(i -> kolonnas.get(i).set(rindasIndekss, rindasVertības.get(i)));
            rinda = RindaI.rinda(this, rindasIndekss);
            jēlasRindas.set(rindasIndekss, rinda);
        }
        ++izmers;
        rindas.add(rinda);
        papildinājumsKlausītājs.forEach(klausītājs -> klausītājs.reģistrē_papildinājumi(rinda));
        return rinda;
    }

    @Override
    public void noņemt(int rindasIndekss) {
        final var noņemšanaNo = jēlasRindas.get(rindasIndekss);
        pirmsNoņemšanaKlausītājs.forEach(klausītājs -> klausītājs.rēgistrē_pirms_noņemšanas(noņemšanaNo));
        kolonnas.forEach(kolonna -> {
            kolonna.set(rindasIndekss, null);
        });
        rindas.remove(jēlasRindas.get(rindasIndekss));
        jēlasRindas.set(rindasIndekss, null);
        indekssiNelitoti.add(rindasIndekss);
        --izmers;
        pēcNoņemšanaAbonēšanas.forEach(klausītājs -> klausītājs.rēgistrē_pirms_noņemšanas(noņemšanaNo));
    }

    @Override
    public void noņemt(Rinda rinda) {
        noņemt(rinda.indekss());
    }

    @Override
    public int izmērs() {
        return izmers;
    }

    @Override
    public void abonē_uz_pēcNoņemšana(PirmsNoņemšanasKlausītājs listener) {
        pēcNoņemšanaAbonēšanas.add(listener);
    }

    /**
     * JAUDA
     *
     * @return
     */
    @Override
    public List<Kolonna<Object>> kolonnaSkats() {
        return listWithValuesOf(kolonnas);
    }

    @Override
    public String toString() {
        return DatuBāze.class.getSimpleName() + path().toString();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        return vecāks.map(Discoverable::path).orElse(list()).withAppended(vārds);
    }

    @Override
    public Element toDom() {
        final var dom = element(DatuBāze.class.getSimpleName());
        jēlaRindasSkats().stream()
                .filter(rinda -> rinda != null)
                .forEach(rinda -> dom.appendChild(rinda.toDom()));
        return dom;
    }

    @Override
    public List<Rinda> jēlasRindas() {
        return listWithValuesOf(rindas);
    }

    @Override
    public Rinda uzmeklēVienādus(Atribūts<Rinda> atribūts, Rinda rinda) {
        return rindas.stream()
                .filter(citaRinda -> citaRinda.vērtība(atribūts).indekss() == rinda.indekss())
                .reduce(StreamUtils.ensureSingle())
                .get();
    }
}
