/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.messages;

public enum RenderingType {
    PLAIN_TEXT("plain-text");
    private String name;

    private RenderingType(String argName) {
        name = argName;
    }
}
