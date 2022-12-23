package net.splitcells.cin;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

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

    private PositionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute) {
        this.xAttribute = xAttribute;
        this.yAttribute = yAttribute;
    }

    @Override
    public Set<List<String>> paths() {
        throw notImplementedYet();
    }

    @Override
    public void addContext(Discoverable context) {
        throw notImplementedYet();
    }

    @Override
    public List<Domable> arguments() {
        throw notImplementedYet();
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
        throw notImplementedYet();
    }
}
