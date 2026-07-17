/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

public class AdminRole implements StaticRole {
    public static final Role ADMIN_ROLE = new AdminRole();
    private AdminRole() {

    }
}
