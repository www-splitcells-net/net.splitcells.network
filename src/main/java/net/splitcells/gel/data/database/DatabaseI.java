package net.splitcells.gel.data.database;

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
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LineI;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnI;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.data.table.column.ColumnViewI;
import org.w3c.dom.Element;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.dem.object.Discoverable;

public class DatabaseI implements Database {
    protected final String vārds;
    protected final Optional<Discoverable> vecāks;
    protected final List<Attribute<Object>> atribūti;
    protected final List<Column<Object>> kolonnas = list();
    protected final Map<Attribute<?>, Integer> tips_kolonnasIndekss = map();
    protected final Set<Line> rindas = setOfUniques();
    protected final List<Line> jēlasRindas = list();
    protected final ListView<Line> jēlasRindasSkats = listView(jēlasRindas);
    protected int izmers;
    protected final List<AfterAdditionSubscriber> papildinājumsKlausītājs = list();
    protected final List<BeforeRemovalSubscriber> pirmsNoņemšanaKlausītājs = list();
    protected final List<BeforeRemovalSubscriber> pēcNoņemšanaAbonēšanas = list();
    protected final net.splitcells.dem.data.set.Set<Integer> indekssiNelitoti = setOfUniques();


    @Deprecated
    public DatabaseI(List<Attribute<? extends Object>> atribūti) {
        this("", null, atribūti.mapped(a -> (Attribute<Object>) a));
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public DatabaseI(String vārds, Discoverable vecāks, List<Attribute<Object>> atribūti) {
        this.vārds = vārds;
        this.vecāks = Optional.ofNullable(vecāks);
        final List<Attribute<Object>> headerAtribūts = list();
        atribūti.forEach(att -> {
            tips_kolonnasIndekss.put(att, headerAtribūts.size());
            headerAtribūts.add(att);
            kolonnas.add(ColumnI.kolonna(this, att));
        });
        this.atribūti = listWithValuesOf(headerAtribūts);
        kolonnas.forEach(this::abonē_uz_papildinājums);
        kolonnas.forEach(this::abonē_uz_iepriekšNoņemšana);
    }

    @Deprecated
    public DatabaseI(List<Attribute<?>> atribūti, Collection<List<Object>> rindasVertības) {
        this(atribūti);
        rindasVertības.forEach(line_values -> pieliktUnPārtulkot(line_values));
    }

    public DatabaseI(String vārds, Discoverable vecāks, Attribute<? extends Object>... atribūti) {
        this(vārds, vecāks, listWithValuesOf(atribūti).mapped(a -> (Attribute<Object>) a));
    }

    @Deprecated
    public DatabaseI(Attribute<?>... atribūti) {
        this(listWithValuesOf(atribūti));
    }

    @Override
    public List<Attribute<Object>> nosaukumuSkats() {
        return atribūti;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ColumnView<T> kolonnaSkats(Attribute<T> atribūts) {
        return ColumnViewI.kolonnasSkats((Column<T>) kolonnas.get(tips_kolonnasIndekss.get(atribūts)));
    }

    @Override
    public ListView<Line> jēlaRindasSkats() {
        return jēlasRindasSkats;
    }

    @Override
    public void abonē_uz_papildinājums(AfterAdditionSubscriber klausītājs) {
        this.papildinājumsKlausītājs.add(klausītājs);
    }

    @Override
    public void abonē_uz_iepriekšNoņemšana(BeforeRemovalSubscriber pirmsNoņemšanasKlausītājs) {
        pirmsNoņemšanaKlausītājs.add(pirmsNoņemšanasKlausītājs);
    }

    @Override
    public Line pielikt(Line rinda) {
        final List<Object> rindasVertības = list();
        range(0, atribūti.size()).forEach(i -> {
            rindasVertības.add(rinda.vērtība(atribūti.get(i)));
        });
        return pieliktTulkošanaNo(rindasVertības, rinda.indekss());
    }

    protected Line pieliktTulkošanaNo(List<Object> rindasVertības, int indekss) {
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
        final var rinda = LineI.rinda(this, indekss);
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
    public Line pieliktUnPārtulkot(List<? extends Object> rindasVertības) {
        final int rindasIndekss;
        final Line rinda;
        if (indekssiNelitoti.isEmpty()) {
            rindasIndekss = jēlasRindas.size();
            rinda = LineI.rinda(this, rindasIndekss);
            jēlasRindas.add(rinda);
            range(0, rindasVertības.size()).forEach(i -> kolonnas.get(i).add(rindasVertības.get(i)));
        } else {
            rindasIndekss = removeAny(indekssiNelitoti);
            range(0, rindasVertības.size()).forEach(i -> kolonnas.get(i).set(rindasIndekss, rindasVertības.get(i)));
            rinda = LineI.rinda(this, rindasIndekss);
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
    public void noņemt(Line rinda) {
        noņemt(rinda.indekss());
    }

    @Override
    public int izmērs() {
        return izmers;
    }

    @Override
    public void abonē_uz_pēcNoņemšana(BeforeRemovalSubscriber listener) {
        pēcNoņemšanaAbonēšanas.add(listener);
    }

    /**
     * JAUDA
     *
     * @return
     */
    @Override
    public List<Column<Object>> kolonnaSkats() {
        return listWithValuesOf(kolonnas);
    }

    @Override
    public String toString() {
        return Database.class.getSimpleName() + path().toString();
    }

    @Override
    public net.splitcells.dem.data.set.list.List<String> path() {
        return vecāks.map(Discoverable::path).orElse(list()).withAppended(vārds);
    }

    @Override
    public Element toDom() {
        final var dom = element(Database.class.getSimpleName());
        jēlaRindasSkats().stream()
                .filter(rinda -> rinda != null)
                .forEach(rinda -> dom.appendChild(rinda.toDom()));
        return dom;
    }

    @Override
    public List<Line> jēlasRindas() {
        return listWithValuesOf(rindas);
    }

    @Override
    public Line uzmeklēVienādus(Attribute<Line> atribūts, Line rinda) {
        return rindas.stream()
                .filter(citaRinda -> citaRinda.vērtība(atribūts).indekss() == rinda.indekss())
                .reduce(StreamUtils.ensureSingle())
                .get();
    }
}
