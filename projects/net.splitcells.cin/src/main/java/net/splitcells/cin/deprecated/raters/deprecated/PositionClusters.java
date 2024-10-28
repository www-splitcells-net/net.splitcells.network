/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.cin.deprecated.raters.deprecated;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.Table;
import net.splitcells.gel.data.view.attribute.Attribute;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.RatingEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.typed.TypedMapI.typedMap;
import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.INCOMING_CONSTRAINT_GROUP;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.framework.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * <p>Groups all positions of a 2 dimensional space.
 * Every existing (x, y) coordinate has multiple corresponding position cluster, that is identified by its center.
 * Every such group has a center position and also contains every neighbouring position.
 * Every existing (x, y) coordinate has exactly one position cluster,
 * where the coordinate is the center position of that cluster.
 * This also applies to neighbouring positions only sharing a corner with the center position.
 * All position clusters are non overlapping.</p>
 * <p>Overlapping position clusters can be implemented via multiple parallel constraint nodes,
 * with different (x,y) offset of the position center.
 * In this case, there is on additional offset for each neighbouring positions of the original center position</p>
 * <p>An (x,y) offset of (0,0), means that the most central position group on the 2D grid is (0,0).
 * Its center is located at (1,1).
 * The neighbouring position group to the right (x direction) is (3,0) and
 * its center position is (4,1).</p>
 * <p>An (x,y) offset of (1,1), means that the most central position group on the 2D grid is (1,1).
 * Its center is located at (2,2).
 * The neighbouring position group to the right (x direction) is (4,1) and
 * its center position is (5,2).</p>
 */
public class PositionClusters implements Rater {

    /**
     * @param x  This is the center x coordinate of the group.
     * @param y  This is the center y coordinate of the group.
     * @param oX This is the center x offset used for determining the center position.
     * @param oY This is the center y offset used for determining the center position.
     * @return Creates the group name of the position cluster, given the center position of the cluster.
     */
    public static String groupNameOfPositionCluster(int x, int y, int oX, int oY) {
        return "(x=" + x + ", y=" + y + ", oX=" + oX + ", oY=" + oY + ")";
    }

    public static int centerXPositionOf(GroupId group) {
        return group.metaData().value(PositionClustersCenterX.class);
    }

    public static int centerYPositionOf(GroupId group) {
        return group.metaData().value(PositionClustersCenterY.class);
    }

    /**
     * @param xAttribute This is the x coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param yAttribute This is the y coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @return Returns the list of all overlapping position clusters.
     */
    public static List<Rater> positionClustering(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute) {
        return list(new PositionClusters(xAttribute, yAttribute, 0, 0),
                new PositionClusters(xAttribute, yAttribute, 0, 1),
                new PositionClusters(xAttribute, yAttribute, 1, 1),
                new PositionClusters(xAttribute, yAttribute, 1, 0),
                new PositionClusters(xAttribute, yAttribute, 1, -1),
                new PositionClusters(xAttribute, yAttribute, 0, -1),
                new PositionClusters(xAttribute, yAttribute, -1, -1),
                new PositionClusters(xAttribute, yAttribute, -1, 0),
                new PositionClusters(xAttribute, yAttribute, -1, 1));
    }

    /**
     * @param xAttribute This is the x coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param yAttribute This is the y coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @return Returns the {@link PositionClusters} with offset of 0.
     */
    public static PositionClusters positionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute) {
        return new PositionClusters(xAttribute, yAttribute, 0, 0);
    }

    /**
     * @param xAttribute    This is the x coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param yAttribute    This is the y coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param xCenterOffset This is the x-axis offset of the position center.
     * @param yCenterOffset This is the y-axis offset of the position center.
     * @return Returns the {@link PositionClusters} with offset of 0.
     */
    public static PositionClusters positionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute, int xCenterOffset, int yCenterOffset) {
        return new PositionClusters(xAttribute, yAttribute, xCenterOffset, yCenterOffset);
    }

    private final Attribute<Integer> xAttribute;
    private final Attribute<Integer> yAttribute;
    private final int xCenterOffset;
    private final int yCenterOffset;

    /**
     * Maps the incoming group id, x and y coordinates to the respective groups.
     * The first key is the x coordinate.
     */
    private final Map<GroupId, Map<Integer, Map<Integer, GroupId>>> positionGroups = map();

    private PositionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute, int xCenterOffset, int yCenterOffset) {
        this.xAttribute = xAttribute;
        this.yAttribute = yAttribute;
        this.xCenterOffset = xCenterOffset;
        this.yCenterOffset = yCenterOffset;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
        final int xValWithoutOffset;
        final int yValWithoutOffset;
        final var xVal = addition.value(LINE).value(xAttribute);
        final var yVal = addition.value(LINE).value(yAttribute);
        if (xVal < 0) {
            xValWithoutOffset = xVal - xCenterOffset;
        } else {
            xValWithoutOffset = xVal + xCenterOffset;
        }
        if (xVal < 0) {
            yValWithoutOffset = yVal - yCenterOffset;
        } else {
            yValWithoutOffset = yVal + yCenterOffset;
        }
        /* The coordinates for the corner of the position cluster, that points to the (0,0) position.
         * These coordinates have no offset.
         */
        final int xCoordInner;
        final int yCoordInner;
        /*
         * The coordinates for the center of the position cluster.
         * These coordinates have the offset.
         */
        final int xCoordCenter;
        final int yCoordCenter;
        final int xAbs = absolute(xValWithoutOffset);
        final int yAbs = absolute(yValWithoutOffset);
        if (xValWithoutOffset < 0) {
            xCoordInner = xValWithoutOffset + modulus(xAbs - 1, 3);
            xCoordCenter = xCoordInner - 1 + xCenterOffset;
        } else {
            xCoordInner = xValWithoutOffset - modulus(xValWithoutOffset, 3);
            xCoordCenter = xCoordInner + 1 - xCenterOffset;
        }
        if (yValWithoutOffset < 0) {
            yCoordInner = yValWithoutOffset + modulus(yAbs - 1, 3);
            yCoordCenter = yCoordInner - 1 + yCenterOffset;
        } else {
            yCoordInner = yValWithoutOffset - modulus(yValWithoutOffset, 3);
            yCoordCenter = yCoordInner + 1 - yCenterOffset;
        }
        final RatingEvent rating = ratingEvent();
        final var incomingGroup = addition.value(INCOMING_CONSTRAINT_GROUP);
        final var positionGroup = positionGroups
                .computeIfAbsent(incomingGroup, icg -> map())
                .computeIfAbsent(xCoordInner, x -> map())
                .computeIfAbsent(yCoordInner, y -> group(incomingGroup, groupNameOfPositionCluster(xCoordCenter, yCoordCenter, xCenterOffset, yCenterOffset)
                        , typedMap().withAssignment(PositionClustersCenterX.class, xCoordCenter)
                                .withAssignment(PositionClustersCenterY.class, yCoordCenter)));
        rating.additions().put(addition, localRating()
                .withPropagationTo(children)
                .withRating(noCost())
                .withResultingGroupId(positionGroup));
        return rating;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children, Table lineProcessing) {
        return ratingEvent();
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return "Clusters lines into positions based on "
                + xAttribute.name()
                + " for x coordinates, "
                + yAttribute.name() + " for y coordinates "
                + ", a center X offset of " + xCenterOffset
                + " and a center Y offset of " + yCenterOffset
                + ".";
    }

    @Override
    public List<Domable> arguments() {
        return list(xAttribute
                , yAttribute
                , tree("" + xCenterOffset)
                , tree("" + yCenterOffset));
    }
}
