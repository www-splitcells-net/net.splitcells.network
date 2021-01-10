package net.splitcells.gel.rating.rater;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.structure.Rating;

public class RaterBasedOnLineValue implements Rater {
    public static Rater rindasVertībasBalstītasUzGrupetajs(String apraksts, Function<Line, Integer> grupetajs) {
        return rindasVertībasBalstītasUzGrupetajs(new Function<>() {
            private final Map<Integer, GroupId> lineNumbering = map();

            @Override
            public GroupId apply(Line arg) {
                return lineNumbering.computeIfAbsent
                        (grupetajs.apply(arg.value(Constraint.LINE))
                                , classification -> GroupId.group(apraksts + ": " + classification));
            }

            @Override
            public String toString() {
                return apraksts;
            }
        });
    }

    public static Rater rindasVertībaBalstītaUzVērtētāju(Function<Line, Rating> vērtētājsBalstītsUzRindasVertības) {
        return new RaterBasedOnLineValue(vērtētājsBalstītsUzRindasVertības, papildinājums -> papildinājums.value(Constraint.INCOMING_CONSTRAINT_GROUP));
    }

    public static Rater rindasVertībasBalstītasUzGrupetajs(Function<Line, GroupId> grupetajsBalstītsUzRindasVertības) {
        return new RaterBasedOnLineValue(papilduRinda -> noCost(), grupetajsBalstītsUzRindasVertības);
    }

    private final Function<Line, Rating> rindasBalstītsUzVertībasVērtētājs;
    private final Function<Line, GroupId> grupetajsBalstītsUzRindasVertības;
    private final List<Discoverable> konteksts = list();

    private RaterBasedOnLineValue(Function<Line, Rating> rindasBalstītsUzVertībasVērtētājs, Function<Line, GroupId> grupetajsBalstītsUzRindasVertības) {
        this.rindasBalstītsUzVertībasVērtētājs = rindasBalstītsUzVertībasVērtētājs;
        this.grupetajsBalstītsUzRindasVertības = grupetajsBalstītsUzRindasVertības;
    }

    @Override
    public RatingEvent rating_after_addition(Table rindas, Line papildinājums, net.splitcells.dem.data.set.list.List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu) {
        final RatingEvent rVal = RatingEventI.ratingEvent();
        rVal.additions().put
                (papildinājums
                        , localRating()
                                .withPropagationTo(bērni)
                                .withResultingGroupId(grupetajsBalstītsUzRindasVertības.apply(papildinājums))
                                .withRating(rindasBalstītsUzVertībasVērtētājs.apply(papildinājums.value(Constraint.LINE))));
        return rVal;
    }

    @Override
    public RatingEvent rating_before_removal(Table rindas, Line noņemšana, net.splitcells.dem.data.set.list.List<Constraint> bērni, Table novērtējumsPirmsNoņemšana) {
        return RatingEventI.ratingEvent();
    }

    @Override
    public Class<? extends Rater> type() {
        return RaterBasedOnLineValue.class;
    }

    @Override
    public net.splitcells.dem.data.set.list.List<Domable> arguments() {
        return list(() -> Xml.element
                ("rindasBalstītsUzVertībasVērtētājs"
                        , Xml.textNode(rindasBalstītsUzVertībasVērtētājs.toString())));
    }

    @Override
    public Node argumentation(GroupId grupa, Table piešķiršanas) {
        final var argumentācija = Xml.element("grupa");
        argumentācija.appendChild
                (Xml.textNode(grupa.vārds().orElse("pazudis-grupas-vards")));
        return argumentācija;
    }

    @Override
    public void addContext(Discoverable konteksts) {
        this.konteksts.add(konteksts);
    }

    @Override
    public Collection<net.splitcells.dem.data.set.list.List<String>> paths() {
        return konteksts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Element toDom() {
        final org.w3c.dom.Element dom = Xml.element(getClass().getSimpleName());
        dom.appendChild(Xml.element("args", arguments().get(0).toDom()));
        return dom;
    }

    @Override
    public String toSimpleDescription(Line rinda, GroupId grupa) {
        return grupetajsBalstītsUzRindasVertības.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + rindasBalstītsUzVertībasVērtētājs + ", " + grupetajsBalstītsUzRindasVertības;
    }
}
