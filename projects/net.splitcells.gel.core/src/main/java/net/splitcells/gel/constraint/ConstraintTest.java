package net.splitcells.gel.constraint;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.type.ForAlls;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;
import net.splitcells.gel.rating.rater.RatingEventI;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static java.util.stream.IntStream.range;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.constraint.type.ForAlls.forAll;
import static net.splitcells.gel.constraint.type.ForAlls.forEach;
import static net.splitcells.gel.data.database.Databases.database;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintTest {
    @Test
    public void testMultipleGroupAssignment() {
        final var attribute = attribute(Integer.class);
        final var lineSupplier = database(attribute);
        final var group1 = group("1");
        final var group2 = group("2");
        final var testSubject = forEach(new Rater() {

            @Override
            public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table ratingsBeforeAddition) {
                final var ratingEvent = ratingEvent();
                ratingEvent.complexAdditions().put(addition
                        , list(localRating().withRating(noCost()).withPropagationTo(children).withResultingGroupId(group1)
                                , localRating().withRating(noCost()).withPropagationTo(children).withResultingGroupId(group2)
                        ));
                return ratingEvent;
            }

            @Override
            public List<Domable> arguments() {
                return list();
            }

            @Override
            public void addContext(Discoverable context) {

            }

            @Override
            public Collection<List<String>> paths() {
                return list();
            }
        });
        final var validator = forAll();
        testSubject.withChildren(validator);
        final List<Line> lines = list();
        {
            lines.withAppended(lineSupplier.addTranslated(list(1)), lineSupplier.addTranslated(list(2)));
            testSubject.register_papildinājumi(lines);
        }
        {
            assertThat(testSubject.defying()).isEmpty();
            assertThat(testSubject.complying()).hasSize(lines.size());
        }
    }
}
