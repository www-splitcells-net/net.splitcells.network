/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.data.lookup;

import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static org.assertj.core.api.Assertions.assertThat;

public class LookupIRef<T> extends LookupI<T> {
    protected LookupIRef(Table table, Attribute attrribute) {
        super(table, attrribute);
    }
    @Override
    public void register_addition(T addition, int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(table.rawLinesView().size()).isGreaterThan(index);
        }
        super.register_addition(addition,index);
    }
    @Override
    public void register_removal(T removal, int index) {
        if (ENFORCING_UNIT_CONSISTENCY) {
            assertThat(content.get(removal).rawLinesView().get(index)).isNotNull();
        }
        super.register_removal(removal, index);
    }
}
