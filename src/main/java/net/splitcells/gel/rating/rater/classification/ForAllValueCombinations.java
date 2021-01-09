package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.rater.RatingEventI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.bezMaksas;
import static net.splitcells.gel.rating.structure.LocalRatingI.lokalsNovērtejums;

import java.util.Collection;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.tabula.Rinda;
import net.splitcells.gel.data.tabula.Tabula;
import net.splitcells.gel.data.tabula.atribūts.Atribūts;
import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.w3c.dom.Node;

public class ForAllValueCombinations implements Rater {
    public static ForAllValueCombinations forAllValueCombinations(final Atribūts<?>... args) {
        return new ForAllValueCombinations(args);
    }

    /**
     * ienākošieGrupasId -> vertības no atribūts -> radītsGrupasId
     */
    private final Map<GrupaId, Map<List<Object>, GrupaId>> grupas = map();
    private final List<Atribūts<?>> atribūti = list();
    private final List<Discoverable> kontekts = list();

    private ForAllValueCombinations(final Atribūts<?>... args) {
        for (final var atribūti : args) {
            this.atribūti.add(atribūti);
        }
    }

    public List<Atribūts<?>> attributes() {
        return Lists.listWithValuesOf(atribūti);
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu
            (Tabula rindas, Rinda papildinājums, List<Ierobežojums> bērni, Tabula novērtējumsPirmsPapildinājumu) {
        final List<Object> grupasVertības = list();
        final var rindasVērtība = papildinājums.vērtība(Ierobežojums.RINDA);
        atribūti.forEach(e -> grupasVertības.add(rindasVērtība.vērtība(e)));
        final var ienākošasGrupasId = papildinājums.vērtība(Ierobežojums.IENĀKOŠIE_IEROBEŽOJUMU_GRUPAS_ID);
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
    public RatingEvent vērtē_pirms_noņemšana(Tabula rindas, Rinda noņemšana, List<Ierobežojums> bērni, Tabula novērtējumsPirmsNoņemšana) {
        return novērtejumuNotikums();
    }

    @Override
    public Class<? extends Rater> type() {
        return ForAllValueCombinations.class;
    }

    @Override
    public List<Domable> arguments() {
        return listWithValuesOf(atribūti.mapped(a -> (Domable) a));
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
            atribūti.forEach(att -> attributeDescription.appendChild(att.toDom()));
        }
        return reasoning;
    }

    @Override
    public String uzVienkāršuAprakstu(Rinda rinda, GrupaId grupa) {
        return "priekš visiem kombinācijas no "
                + atribūti
                .stream()
                .map(a -> a.vārds())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
