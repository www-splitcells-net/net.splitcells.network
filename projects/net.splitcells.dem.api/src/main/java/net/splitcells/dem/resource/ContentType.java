/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.splitcells.dem.data.Flows;

import java.util.Optional;

import static net.splitcells.dem.data.Flows.flow;

@Accessors(fluent = true)
public enum ContentType {
    BINARY("application/octet-stream", true),
    COMMON_MARK("text/markdown", false),
    CSV("csv", false),
    UTF_8("UTF-8", false),
    HTML_TEXT("text/html", false),
    CSS("text/css", false),
    JS("text/javascript", false),
    TEXT("text/plain", false),
    XML("application/xml", false),
    ZIP("application/zip", true),
    JSON("application/json", false),
    GIF("image/gif", true);

    /**
     * TODO rename this to mime type of something like that.
     */
    @Getter private final String codeName;
    @Getter private final boolean isBinary;

    ContentType(String argCodeName, boolean argIsBinary) {
        codeName = argCodeName;
        isBinary = argIsBinary;
    }

    public static Optional<ContentType> parseOptionally(String arg) {
        return flow(values()).filter(v -> v.codeName().equals(arg)).findFirst();
    }
}
