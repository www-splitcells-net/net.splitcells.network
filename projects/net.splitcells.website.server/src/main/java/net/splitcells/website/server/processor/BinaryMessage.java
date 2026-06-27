/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.processor;

import lombok.val;
import net.splitcells.website.Format;

import static net.splitcells.dem.lang.tree.TreeI.tree;
import static net.splitcells.dem.resource.ContentType.parseOptionally;

public class BinaryMessage {
    /**
     * TODO It is unclear, if the usage of Strings for the format ID is a good idea.
     *
     * @param content
     * @param format
     * @return
     */
    @Deprecated
    public static BinaryMessage binaryMessage(byte[] content, String format) {
        return new BinaryMessage(content, format);
    }

    public static BinaryMessage binaryMessage(byte[] content, Format format) {
        return new BinaryMessage(content, format.mimeTypes());
    }

    private final byte[] content;
    private final String format;

    private BinaryMessage(byte[] content, String format) {
        this.content = content;
        this.format = format;
    }

    public byte[] getContent() {
        return content;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        val rVal = tree("RenderingResult:").withProperty("format", format);
        val formatType = parseOptionally(format);
        if (formatType.isPresent() && !formatType.get().isBinary()) {
            rVal.withProperty("content", new String(content));
        } else {
            rVal.withProperty("content", "[binary content]");
        }
        return rVal.toXmlString();
    }
}
