package net.splitcells.gel.rating.rater;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.structure.LocalRatingI.localRating;

import java.util.Collection;
import java.util.function.Function;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.common.Language;
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
    public static Rater raterBasedOnLineValue(String description, Function<Line, Integer> classifier) {
        return raterBasedOnLineValue(new Function<>() {
            private final Map<Integer, GroupId> lineNumbering = map();

            @Override
            public GroupId apply(Line arg) {
                return lineNumbering.computeIfAbsent
                        (classifier.apply(arg.value(Constraint.LINE))
                                , classification -> GroupId.group(description + ": " + classification));
            }

            @Override
            public String toString() {
                return description;
            }
        });
    }

    public static Rater lineValueBasedOnRater(Function<Line, Rating> raterBasedOnLineValue) {
        return new RaterBasedOnLineValue(raterBasedOnLineValue
                , papildinājums -> papildinājums.value(Constraint.INCOMING_CONSTRAINT_GROUP));
    }

    public static Rater raterBasedOnLineValue(Function<Line, GroupId> clasifierBasedOnLineValue) {
        return new RaterBasedOnLineValue(additionalLine -> noCost(), clasifierBasedOnLineValue);
    }

    private final Function<Line, Rating> raterBasedOnLineValue;
    private final Function<Line, GroupId> classifierBasedOnLineValue;
    private final List<Discoverable> contexts = list();

    private RaterBasedOnLineValue(Function<Line, Rating> raterBasedOnLineValue
            , Function<Line, GroupId> classifierBasedOnLineValue) {
        this.raterBasedOnLineValue = raterBasedOnLineValue;
        this.classifierBasedOnLineValue = classifierBasedOnLineValue;
    }

    @Override
    public RatingEvent rating_after_addition(Table lines, Line addition, List<Constraint> children
            , Table ratingsBeforeAddition) {
        final RatingEvent rating = ratingEvent();
        rating.additions().put
                (addition
                        , localRating()
                                .withPropagationTo(children)
                                .withResultingGroupId(classifierBasedOnLineValue.apply(addition))
                                .withRating(raterBasedOnLineValue.apply(addition.value(Constraint.LINE))));
        return rating;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children
            , Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Class<? extends Rater> type() {
        return RaterBasedOnLineValue.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(() -> Xml.element(getClass().getSimpleName(), Xml.textNode(raterBasedOnLineValue.toString())));
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var argumentation = Xml.element(Language.GROUP.value());
        argumentation.appendChild
                (Xml.textNode(group.vārds().orElse("missing-group-name")));
        return argumentation;
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
    public Element toDom() {
        final Element dom = Xml.element(getClass().getSimpleName());
        dom.appendChild(Xml.element("args", arguments().get(0).toDom()));
        return dom;
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return classifierBasedOnLineValue.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + raterBasedOnLineValue + ", " + classifierBasedOnLineValue;
    }
}
