/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.editor;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.splitcells.website.Format;

import static net.splitcells.dem.data.set.map.Maps.map;

@Accessors(chain = true)
public class EditorData {
    public static EditorData editorData(Format format, byte[] content) {
        return new EditorData(format, content);
    }

    @Getter @Setter private Format format;
    @Getter @Setter private byte[] content;

    private EditorData(Format argFormat, byte[] argContent) {
        format = argFormat;
        content = argContent;
    }
}
