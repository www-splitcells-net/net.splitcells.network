package net.splitcells.gel.constraint.type.framework;

import static net.splitcells.dem.data.set.list.Lists.list;

import java.util.function.Function;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

@Deprecated
public abstract class ConstraintBasedOnLocalGroupsAI extends ConstraintAI {
    protected final Rater rater;

    protected ConstraintBasedOnLocalGroupsAI(Function<Constraint, Rater> raterFactory) {
        super(Constraint.standardGroup());
        rater = raterFactory.apply(this);
    }

    protected ConstraintBasedOnLocalGroupsAI(Rater rater, String name) {
        this(Constraint.standardGroup(), rater, name);
    }

    protected ConstraintBasedOnLocalGroupsAI(Rater rater) {
        this(Constraint.standardGroup(), rater, "");
    }

    protected ConstraintBasedOnLocalGroupsAI(GroupId standardGroup, Rater rater, String name) {
        super(standardGroup, name);
        this.rater = rater;
    }

    @Override
    public void process_line_addition(Line addition) {
        final var incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        processRatingEvent(
                rater.ratingAfterAddition(
                        lines.columnView(INCOMING_CONSTRAINT_GROUP)
                                .lookup(incomingGroup)
                        , addition
                        , children
                        , lineProcessing
                                .columnView(INCOMING_CONSTRAINT_GROUP)
                                .lookup(incomingGroup)));
    }

    protected void processRatingEvent(RatingEvent ratingEvent) {
        ratingEvent.removal().forEach(removal ->
                lineProcessing.allocations_of_demand(removal).forEach(lineProcessing::remove));
        ratingEvent.additions().forEach((line, resultUpdate) -> {
            final var r = addResult(resultUpdate);
            int i = r.index();
            lineProcessing.allocate(line, r);
        });
    }

    @Override
    protected void process_lines_beforeRemoval(GroupId incomingGroup, Line removal) {
        processRatingEvent(
                rater.rating_before_removal(
                        lines.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)
                        , lines.columnView(INCOMING_CONSTRAINT_GROUP)
                                .lookup(incomingGroup)
                                .columnView(LINE)
                                .lookup(removal)
                                .getLines(0)
                        , children
                        , lineProcessing.columnView(INCOMING_CONSTRAINT_GROUP).lookup(incomingGroup)));
        super.process_lines_beforeRemoval(incomingGroup, removal);
    }

    @Override
    public List<String> path() {
        return mainContext
                .map(context -> context.path())
                .orElseGet(() -> list())
                .withAppended(this.getClass().getSimpleName());
    }

    @Override
    public List<Domable> arguments() {
        return list(rater);
    }

}