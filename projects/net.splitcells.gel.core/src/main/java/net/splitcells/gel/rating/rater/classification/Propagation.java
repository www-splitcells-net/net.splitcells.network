package net.splitcells.gel.rating.rater.classification;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.utils.NotImplementedYet.not_implemented_yet;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.common.Language.PROPAGTION;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.Xml;
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
    public static Propagation propagation() {
        return new Propagation();
    }

    private Propagation() {
    }

    private final List<Discoverable> contexts = list();

    @Override
    public RatingEvent ratingAfterAddition
            (Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
        final RatingEvent ratingEvent = ratingEvent();
        ratingEvent.additions().put
                (addition
                        , localRating()
                                .withPropagationTo(children)
                                .withRating(noCost())
                                .withResultingGroupId(addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)));
        return ratingEvent;
    }

    @Override
    public RatingEvent rating_before_removal
            (Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        return Xml.elementWithChildren(PROPAGTION.value());
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return "";
    }

    @Override
    public List<Domable> arguments() {
        return list();
    }

    @Override
    public void addContext(Discoverable contexts) {
        this.contexts.add(contexts);
    }

    @Override
    public Collection<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public Class<? extends Rater> type() {
        return Propagation.class;
    }
}
