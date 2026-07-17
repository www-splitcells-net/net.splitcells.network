/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.framework;

import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.view.Line;
import net.splitcells.gel.data.view.View;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.TODO_NOT_IMPLEMENTED_YET;

public interface SimpleDescriptor {
    String toSimpleDescription(Line line, View groupLineProcessing, GroupId incomingGroup);
    default Tree toPerspective() {
        return tree(TODO_NOT_IMPLEMENTED_YET + toString());
    }
    String descriptivePathName();
}
