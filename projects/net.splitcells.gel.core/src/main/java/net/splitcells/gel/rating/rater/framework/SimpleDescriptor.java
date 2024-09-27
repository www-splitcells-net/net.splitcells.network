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
package net.splitcells.gel.rating.rater.framework;

import net.splitcells.dem.lang.perspective.Tree;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;

import static net.splitcells.dem.lang.perspective.TreeI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.TODO_NOT_IMPLEMENTED_YET;

@FunctionalInterface
public interface SimpleDescriptor {
    String toSimpleDescription(Line line, Table groupLineProcessing, GroupId incomingGroup);
    default Tree toPerspective() {
        return perspective(TODO_NOT_IMPLEMENTED_YET + toString());
    }
}
