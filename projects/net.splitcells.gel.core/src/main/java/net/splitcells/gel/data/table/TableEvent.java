/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.data.table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.data.table.history.TableEventType;
import net.splitcells.gel.data.view.Line;

import java.util.Optional;

@Accessors(chain = true)
public class TableEvent {
    public static TableEvent tableEvent(Line argLine, TableEventType argType) {
        return new TableEvent(argLine, argType);
    }

    @Getter private Line line;
    @Getter private TableEventType type;
    @Getter @Setter private Optional<Tree> reason = Optional.empty();

    private TableEvent(Line argLine, TableEventType argType) {
        line = argLine;
        type = argType;
    }
    
    public TableEvent setReason(Tree arg) {
        reason = Optional.of(arg);
        return this;
    }
}
