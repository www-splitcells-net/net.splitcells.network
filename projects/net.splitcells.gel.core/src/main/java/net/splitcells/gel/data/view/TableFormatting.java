/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.view;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.view.attribute.Attribute;

import static net.splitcells.dem.data.set.list.Lists.list;

@Accessors(chain = true)
public class TableFormatting {
    public static TableFormatting tableFormat() {
        return new TableFormatting();
    }

    @Getter @Setter private List<Attribute<?>> columnAttributes = list();
    @Getter @Setter private List<Attribute<?>> rowAttributes = list();

    private TableFormatting() {
    }
}
