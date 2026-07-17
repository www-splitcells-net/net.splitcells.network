/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website;

import lombok.val;
import net.splitcells.dem.resource.ContentType;
import net.splitcells.website.server.project.renderer.extension.commonmark.CommonMarkIntegration;

import java.util.Optional;

import static net.splitcells.dem.utils.ExecutionException.execException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

/**
 * TODO REMOVE This is a duplicate of {@link ContentType}.
 */
@Deprecated
public enum Format {
    @Deprecated BINARY("application/octet-stream"),
    @Deprecated HTML("text/html"),
    @Deprecated CSS("text/css"),
    @Deprecated CSV("text/csv"),
    @Deprecated TEXT_PLAIN("text/plain"),
    @Deprecated COMMON_MARK("text/markdown"),
    @Deprecated JSON("application/json"),
    @Deprecated XML("application/xml"),
    @Deprecated ZIP("application/zip");


    private final String mimeTypes;

    Format(String mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public String mimeTypes() {
        return mimeTypes;
    }
}
