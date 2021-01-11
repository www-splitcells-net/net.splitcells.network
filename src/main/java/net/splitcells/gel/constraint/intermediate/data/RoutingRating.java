package net.splitcells.gel.constraint.intermediate.data;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.rating.structure.Rating;

import java.util.Set;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;

public class RoutingRating {
    public static RoutingRating veidot() {
        return new RoutingRating();
    }
    private RoutingRating() {

    }
    private final List<Rating> novērtējums = list();
    private final Map<Constraint, Set<GroupId>> bērnusUzGrupas = map();

    public List<Rating> getEvents() {
        return novērtējums;
    }

    public Map<Constraint, Set<GroupId>> getChildrenToGroups() {
        return bērnusUzGrupas;
    }
}