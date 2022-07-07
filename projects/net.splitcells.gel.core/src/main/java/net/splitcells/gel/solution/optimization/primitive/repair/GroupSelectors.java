package net.splitcells.gel.solution.optimization.primitive.repair;

import net.splitcells.dem.utils.random.Randomness;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.constraint.Constraint.incomingGroupsOfConstraintPath;

public class GroupSelectors {
    private GroupSelectors() {
        throw constructorIllegal();
    }

    /**
     * This helper method, makes it easier to create a {@link GroupSelector} instead of a {@link FluentGroupSelector}
     * with a lambda and without casting.
     * 
     * @param groupSelector This is the {@link GroupSelector}, which is typically defined in a lambda.
     * @return This is the given argument.
     */
    public static GroupSelector groupSelector(GroupSelector groupSelector) {
        return groupSelector;
    }

    public static GroupSelector groupSelector(Randomness randomness, int minimum_constraint_group_path
            , int numberOfGroupsSelectedPerDefiance) {
        return allocationsGroups -> {
            final var candidates = allocationsGroups
                    .stream()
                    .filter(allocationGroupsPath ->
                            {
                                if (allocationGroupsPath.size() < minimum_constraint_group_path) {
                                    return false;
                                }
                                return incomingGroupsOfConstraintPath(allocationGroupsPath.shallowCopy())
                                        .stream()
                                        .map(group -> !allocationGroupsPath
                                                .lastValue()
                                                .get()
                                                .defying(group)
                                                .isEmpty())
                                        .reduce((a, b) -> a || b)
                                        .orElse(false);
                            }
                    )
                    .collect(toList());
            if (candidates.isEmpty()) {
                return list();
            }
            return randomness.chooseAtMostMultipleOf(numberOfGroupsSelectedPerDefiance, candidates);
        };
    }
}
