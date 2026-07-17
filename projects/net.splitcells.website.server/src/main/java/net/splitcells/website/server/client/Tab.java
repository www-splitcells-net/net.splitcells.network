/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.client;

import net.splitcells.dem.resource.communication.Closeable;

public interface Tab extends Closeable {
    Element elementByClass(String cssClass);

    Element elementById(String id);
}
