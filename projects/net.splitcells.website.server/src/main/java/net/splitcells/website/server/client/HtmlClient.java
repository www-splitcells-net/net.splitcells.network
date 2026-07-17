/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.client;

import net.splitcells.dem.environment.resource.Resource;

public interface HtmlClient extends Resource {
    /**
     * TODO Tab should be probably be renamed to something like site, page or session,
     * as Tab is a GUI specific word.
     *
     * @param path
     * @return
     */
    Tab openTab(String path);
}
