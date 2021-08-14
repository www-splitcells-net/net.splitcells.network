/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.gel.solution.history;

import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;

import net.splitcells.gel.solution.history.event.Allocation;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.solution.history.meta.MetaDataView;

public interface History extends Allocations, AfterAdditionSubscriber, BeforeRemovalSubscriber {
    Attribute<Integer> ALLOCATION_ID = attribute(Integer.class, "allocation-id");
    Attribute<Allocation> ALLOCATION_EVENT = attribute(Allocation.class, "allocation-notikums");
    Attribute<MetaDataView> META_DATA = attribute(MetaDataView.class, "meta-data");

    void resetTo(int index);

    int currentIndex();
}
