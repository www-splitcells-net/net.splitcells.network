/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.processor;

import net.splitcells.website.Formats;

import static net.splitcells.dem.lang.perspective.TreeI.perspective;

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

    public static BinaryMessage binaryMessage(byte[] content, Formats format) {
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
        return perspective("RenderingResult:")
                .withProperty("format", format)
                .withProperty("content", new String(content))
                .toXmlString();
    }
}
