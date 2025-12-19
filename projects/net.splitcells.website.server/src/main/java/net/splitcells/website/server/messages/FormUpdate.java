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
import net.splitcells.dem.utils.StringUtils;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.parseString;

/**
 * This is a message providing updates to a form,
 * by replacing appropriate values.
 * This is primarily used in order to update GUI clients for HTML forms.
 * Every {@link Map} provides info in its values regarding the fields,
 * that are identified by the string keys.
 */
@Accessors(chain = true)
public class FormUpdate {
    public static FormUpdate formUpdate() {
        return new FormUpdate();
    }


    @Getter @Setter private Map<String, FieldUpdate> fields = map();

    private FormUpdate() {

    }

    public Tree toTree() {
        val tree = tree("net-splitcells-website-server-form-update");
        val dataValues = tree("data-values").withParent(tree);
        val dataTypes = tree("data-types").withParent(tree);
        val renderingTypes = tree("rendering-types").withParent(tree);
        fields.entrySet().forEach(entry -> {
            dataValues.withProperty(entry.getKey(), parseString(entry.getValue().getData()));
            dataTypes.withProperty(entry.getKey(), entry.getValue().getType().mimeTypes());
            entry.getValue().getRenderingTypes().ifPresent(rendering ->
                    renderingTypes.withProperty(entry.getKey(), rendering.name()));
        });
        return tree;
    }
}
