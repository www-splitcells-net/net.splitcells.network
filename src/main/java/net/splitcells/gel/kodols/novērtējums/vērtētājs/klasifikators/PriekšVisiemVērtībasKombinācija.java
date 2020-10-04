package net.splitcells.gel.kodols.novērtējums.vērtētājs.klasifikators;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.kodols.ierobežojums.Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID;
import static net.splitcells.gel.kodols.ierobežojums.Ierobežojums.RINDA;
import static net.splitcells.gel.kodols.novērtējums.vērtētājs.NovērtējumsNotikumsI.novērtejumuNotikums;
import static net.splitcells.gel.kodols.novērtējums.tips.Cena.bezMaksas;
import static net.splitcells.gel.kodols.novērtējums.struktūra.VietējieNovērtējumsI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.ierobežojums.GrupaId;
import net.splitcells.gel.kodols.dati.tabula.Rinda;
import net.splitcells.gel.kodols.dati.tabula.Tabula;
import net.splitcells.gel.kodols.dati.tabula.atribūts.Atribūts;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.Vērtētājs;
import net.splitcells.gel.kodols.novērtējums.vērtētājs.NovērtējumsNotikums;
import org.w3c.dom.Node;

public class PriekšVisiemVērtībasKombinācija implements Vērtētājs {
    public static PriekšVisiemVērtībasKombinācija forAllValueCombinations(final Atribūts<?>... args) {
        return new PriekšVisiemVērtībasKombinācija(args);
    }

    /**
     * ienākošieGrupasId -> vertības no atribūts -> radītsGrupasId
     */
    private final Map<GrupaId, Map<List<Object>, GrupaId>> grupas = map();
    private final List<Atribūts<?>> atribūts = list();
    private final List<Discoverable> kontekts = list();

    private PriekšVisiemVērtībasKombinācija(final Atribūts<?>... args) {
        for (final var atribūti : args) {
            atribūts.add(atribūti);
        }
    }

    public List<Atribūts<?>> attributes() {
        return Lists.listWithValuesOf(atribūts);
    }

    @Override
    public NovērtējumsNotikums vērtē_pēc_papildinājumu
            (Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final List<Object> grupasVertības = list();
        final var rindasVērtība = papildinājums.vērtība(RINDA);
        atribūts.forEach(e -> grupasVertības.add(rindasVērtība.vērtība(e)));
        final var ienākošasGrupasId = papildinājums.vērtība(IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
        if (!grupas.containsKey(ienākošasGrupasId)) {
            grupas.put(ienākošasGrupasId, map());
        }
        if (!grupas.get(ienākošasGrupasId).containsKey(grupasVertības)) {
            grupas.get(ienākošasGrupasId).put(grupasVertības
                    , GrupaId.grupa(
                            grupasVertības.stream()
                                    .map(value -> value.toString()).reduce((a, b) -> a + "," + b)
                                    .orElse("tukšs")));
        }
        final var novērtejumuNotikums = novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put(
                papildinājums
                , lokalsNovērtejums()
                        .arIzdalīšanaUz(bērni)
                        .arNovērtējumu(bezMaksas())
                        .arRadītuGrupasId
                                (grupas.get(ienākošasGrupasId).get(grupasVertības)));
        return novērtejumuNotikums;
    }

    @Override
    public NovērtējumsNotikums vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Class<? extends Vērtētājs> type() {
        return PriekšVisiemVērtībasKombinācija.class;
    }

    @Override
    public List<Domable> arguments() {
        return listWithValuesOf(atribūts.mapped(a -> (Domable) a));
    }

    @Override
    public void addContext(Discoverable kontekts) {
        this.kontekts.add(kontekts);
    }

    @Override
    public Collection<List<String>> paths() {
        return kontekts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Node argumentacija(GrupaId grupa, Tabula piešķiršanas) {
        final var reasoning = Xml.element(getClass().getSimpleName());
        {
            final var attributeDescription = Xml.element("atribūts");
            reasoning.appendChild(attributeDescription);
            atribūts.forEach(att -> attributeDescription.appendChild(att.toDom()));
        }
        return reasoning;
    }
}
