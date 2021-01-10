package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.lang.Xml.element;
import static net.splitcells.dem.utils.Not_implemented_yet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

public class Propagation implements Rater {
    public static Propagation izdalīšana() {
        return new Propagation();
    }

    private Propagation() {
    }

    private final List<Discoverable> konteksti = list();

    @Override
    public RatingEvent rating_after_addition
            (Table rindas, Line papildinājums, List<Constraint> bērni, Table novērtējumsPirmsPapildinājumu) {
        final RatingEvent novērtejumuNotikums = ratingEvent();
        novērtejumuNotikums.additions().put
                (papildinājums
                        , localRating()
                                .withPropagationTo(bērni)
                                .withRating(noCost())
                                .withResultingGroupId(papildinājums.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        return novērtejumuNotikums;
    }

    @Override
    public RatingEvent rating_before_removal
            (Table rindas, Line noņemšana, List<Constraint> bērni, Table novērtējumsPirmsNoņemšana) {
        return ratingEvent();
    }

    @Override
    public Node argumentation(GroupId grupa, Table piešķiršanas) {
        return element("izdalīšana");
    }

    @Override
    public String toSimpleDescription(Line rinda, GroupId grupa) {
        return "";
    }

    @Override
    public List<Domable> arguments() {
        return list();
    }

    @Override
    public void addContext(Discoverable konteksts) {
        this.konteksti.add(konteksts);
    }

    @Override
    public Collection<List<String>> paths() {
        return konteksti.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
