/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.assignment;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.view.Line;

import java.util.Optional;

@Accessors(chain = true)
public class AssignmentEvent {
    public static AssignmentEvent assignmentEvent(Line argDemand, Line argSupply, AssignmentEventType argType) {
        return new AssignmentEvent(argDemand, argSupply, argType);
    }

    @Getter private Line demand;
    @Getter private Line supply;
    @Getter private AssignmentEventType type;
    @Getter @Setter private Optional<Tree> reason = Optional.empty();

    private AssignmentEvent(Line argDemand, Line argSupply, AssignmentEventType argType) {
        demand = argDemand;
        supply = argSupply;
        type = argType;
    }

    public AssignmentEvent setReason(Tree arg) {
        reason = Optional.of(arg);
        return this;
    }
}
