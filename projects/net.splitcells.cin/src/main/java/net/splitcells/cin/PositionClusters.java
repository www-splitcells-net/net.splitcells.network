package net.splitcells.cin;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.dem.utils.MathUtils;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * Groups all positions of a 2 dimensional space.
 * Every existing (x, y) coordinate has a corresponding position group.
 * Every such group also contains every neighbouring position.
 * This also applies to neighbouring positions only sharing a corner with the center position.
 */
public class PositionClusters implements Rater {

    /**
     * @param x This is the x coordinate of the group.
     * @param y This is the y coordinate of the group.
     * @return Creates the group name of the position cluster, given the center position of the cluster.
     */
    public static String groupNameOfPositionCluster(int x, int y) {
        return "(x=" + x + ", y=" + y + ")";
    }

    public static PositionClusters positionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute) {
        return new PositionClusters(xAttribute, yAttribute);
    }

    private final Attribute<Integer> xAttribute;
    private final Attribute<Integer> yAttribute;

    /**
     * Maps x and y coordinates to the respective groups.
     * The first key is the x coordinate.
     */
    private final Map<Integer, Map<Integer, GroupId>> positionGroups = map();

    private PositionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute) {
        this.xAttribute = xAttribute;
        this.yAttribute = yAttribute;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
        final var xVal = addition.value(LINE).value(xAttribute);
        final var yVal = addition.value(LINE).value(yAttribute);
        final var xCoord = MathUtils.modulus(xVal, 3);
        final var yCoord = MathUtils.modulus(yVal, 3);
        final RatingEvent rating = ratingEvent();
        final var positionGroup = positionGroups
                .computeIfAbsent(xCoord, x -> map())
                .computeIfAbsent(yCoord, y -> group(groupNameOfPositionCluster(xCoord, yCoord)));
        rating.additions().put(addition, localRating()
                .withPropagationTo(children)
                .withRating(noCost())
                .withResultingGroupId(positionGroup));
        return rating;
    }

    @Override
    public List<Domable> arguments() {
        return list();
    }
}
