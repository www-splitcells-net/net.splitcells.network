/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.messages;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.dem.lang.tree.TreeI;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * This is a message providing updates to a form,
 * by replacing appropriate values.
 * This is primarily used in order to update GUI clients for HTML forms.
 * Every {@link Map} provides info in its values regarding the fields,
 * that are identified by the string keys.
 */
@Accessors(chain = true)
public class FormUpdate {
    public static FormUpdate editorUpdate() {
        return new FormUpdate();
    }


    private @Getter @Setter Map<String, FieldUpdate> fields;

    private FormUpdate() {

    }

    public Tree toTree() {
        val tree = TreeI.tree("net-splitcells-website-server-form-update");
        if (true) {
            throw notImplementedYet();
        }
        return tree;
    }
}
