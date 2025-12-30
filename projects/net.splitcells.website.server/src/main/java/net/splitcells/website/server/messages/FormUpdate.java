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

import static net.splitcells.dem.data.set.map.Maps.map;
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
    public static final String FORM_UPDATE = "net-splitcells-website-server-form-update";
    public static final String DATA_VALUES = "data-values";
    public static final String DATA_TYPES = "data-types";
    public static final String RENDERING_TYPES = "rendering-types";
    public static final String ERRORS = "errors";
    public static final String INTERACTIVE_TABLE = "interactive-table";
    public static final String PLAIN_TEXT = "plain-text";

    public static FormUpdate formUpdate() {
        return new FormUpdate();
    }


    @Getter @Setter private Map<String, FieldUpdate> fields = map();

    private FormUpdate() {

    }

    public Tree toTree() {
        val tree = tree(FORM_UPDATE);
        val dataValues = tree(DATA_VALUES).withParent(tree);
        val dataTypes = tree(DATA_TYPES).withParent(tree);
        val renderingTypes = tree(RENDERING_TYPES).withParent(tree);
        fields.entrySet().forEach(entry -> {
            dataValues.withProperty(entry.getKey(), parseString(entry.getValue().getData()));
            dataTypes.withProperty(entry.getKey(), entry.getValue().getType().mimeTypes());
            entry.getValue().getRenderingType().ifPresent(rendering ->
                    renderingTypes.withProperty(entry.getKey(), rendering.getName()));
        });
        return tree;
    }
}
