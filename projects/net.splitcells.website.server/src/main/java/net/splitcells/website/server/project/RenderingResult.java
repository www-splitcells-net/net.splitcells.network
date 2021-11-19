/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website.server.project;

public class RenderingResult {
    public static RenderingResult renderingResult(byte[] content, String format) {
        return new RenderingResult(content, format);
    }

    private final byte[] content;
    private final String format;

    private RenderingResult(byte[] content, String format) {
        this.content = content;
        this.format = format;
    }

    public byte[] getContent() {
        return content;
    }

    public String getFormat() {
        return format;
    }
}
