package net.splitcells.gel.rating.rater;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.DiscoverableFromMultiplePathsSetter;
import net.splitcells.dem.utils.reflection.PubliclyConstructed;
import net.splitcells.dem.utils.reflection.PubliclyTyped;

/**
 * TODO RENAME Rater seems to be an incorrect name, because it produces more than a rating.
 */
public interface Rater extends PubliclyTyped<Rater>
        , PubliclyConstructed<Domable>
        , DiscoverableFromMultiplePathsSetter
        , Domable {

    /**
     *
     * @param lines TODO Why is this needed? Is this not contained in other variables?
     * @param addition
     * @param children
     * @param ratingsBeforeAddition
     * @return
     */
    RatingEvent ratingAfterAddition(Table  lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition);

    /**
     *
     * @param lines TODO Why is this needed. Is this not contained in other variables?
     * @param removal
     * @param children
     * @param ratingsBeforeRemoval
     * @return
     */
    RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table ratingsBeforeRemoval);

    @Override
    default Class<? extends Rater> type() {
        return Rater.class;
    }

    @Deprecated
    default Node argumentation(GroupId group, Table allocations) {
        throw notImplementedYet(getClass().getName());
    }

    @Override
    default Node toDom() {
        final var dom = Xml.elementWithChildren(getClass().getSimpleName());
        if (!arguments().isEmpty()) {
            dom.appendChild(Xml.element2(Language.ARGUMENTATION.value(), arguments().stream().map(arg -> arg.toDom())));
        }
        return dom;
    }
    
    default String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        throw notImplementedYet(getClass().getName());
    }
}
