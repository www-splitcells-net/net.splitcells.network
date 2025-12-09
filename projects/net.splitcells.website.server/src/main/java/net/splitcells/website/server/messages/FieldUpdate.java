/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.messages;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.website.Format;

import java.util.Optional;

import static net.splitcells.website.Format.BINARY;
import static net.splitcells.website.server.messages.RenderingType.PLAIN_TEXT;

@Accessors(chain = true)
public class FieldUpdate {
    public static FieldUpdate fieldUiUpdate() {
        return new FieldUpdate();
    }

    private FieldUpdate() {

    }

    /**
     * States how a field should be rendered.
     */
    @Getter @Setter private Optional<RenderingType> renderingTypes = Optional.empty();
    /**
     * Contains the data of a field.
     */
    @Getter @Setter private byte[] data = new byte[0];
    /**
     * States the format of a fields data value.
     */
    @Getter @Setter private Format type = BINARY;
}
