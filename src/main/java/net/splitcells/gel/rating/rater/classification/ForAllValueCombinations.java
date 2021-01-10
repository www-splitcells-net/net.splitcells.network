package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.rater.RatingEventI.novērtejumuNotikums;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import org.w3c.dom.Node;

public class ForAllValueCombinations implements Rater {
    public static ForAllValueCombinations forAllValueCombinations(final Attribute<?>... args) {
        return new ForAllValueCombinations(args);
    }

    /**
     * ienākošieGrupasId -> vertības no atribūts -> radītsGrupasId
     */
    private final Map<GroupId, Map<List<Object>, GroupId>> grupas = map();
    private final List<Attribute<?>> atribūti = list();
    private final List<Discoverable> kontekts = list();

    private ForAllValueCombinations(final Attribute<?>... args) {
        for (final var atribūti : args) {
            this.atribūti.add(atribūti);
        }
    }

    public List<Attribute<?>> attributes() {
        return Lists.listWithValuesOf(atribūti);
    }

    @Override
    public RatingEvent vērtē_pēc_papildinājumu
            (Table rindas, Line papildinājums, List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu) {
        final List<Object> grupasVertības = list();
        final var rindasVērtība = papildinājums.value(Constraint.LINE);
        atribūti.forEach(e -> grupasVertības.add(rindasVērtība.value(e)));
        final var ienākošasGrupasId = papildinājums.value(Constraint.INCOMING_CONSTRAINT_GROUP_ID);
        if (!grupas.containsKey(ienākošasGrupasId)) {
            grupas.put(ienākošasGrupasId, map());
        }
        if (!grupas.get(ienākošasGrupasId).containsKey(grupasVertības)) {
            grupas.get(ienākošasGrupasId).put(grupasVertības
                    , GroupId.grupa(
                            grupasVertības.stream()
                                    .map(value -> value.toString()).reduce((a, b) -> a + "," + b)
                                    .orElse("tukšs")));
        }
        final var novērtejumuNotikums = novērtejumuNotikums();
        novērtejumuNotikums.papildinājumi().put(
                papildinājums
                , localRating()
                        .withPropagationTo(bērni)
                        .withRating(noCost())
                        .withResultingGroupId
                                (grupas.get(ienākošasGrupasId).get(grupasVertības)));
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent vērtē_pirms_noņemšana(Table rindas, Line noņemšana, List<Constraint> bērni, Table novērtējumsPirmsNoņemšana) {
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
    public Node argumentacija(GroupId grupa, Table piešķiršanas) {
        final var reasoning = Xml.element(getClass().getSimpleName());
        {
            final var attributeDescription = Xml.element("atribūts");
            reasoning.appendChild(attributeDescription);
            atribūti.forEach(att -> attributeDescription.appendChild(att.toDom()));
        }
        return reasoning;
    }

    @Override
    public String uzVienkāršuAprakstu(Line rinda, GroupId grupa) {
        return "priekš visiem kombinācijas no "
                + atribūti
                .stream()
                .map(a -> a.vārds())
                .reduce((a, b) -> a + " " + b)
                .orElse("");
    }
}
