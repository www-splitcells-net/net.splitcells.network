/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.vertx;

import io.vertx.core.impl.NoStackTraceThrowable;
import net.splitcells.dem.lang.annotations.JavaLegacy;

@JavaLegacy
public class DocumentNotFound extends NoStackTraceThrowable {
    public DocumentNotFound(String message) {
        super(message);
    }
}
