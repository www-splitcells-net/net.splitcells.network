/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.access;

import net.splitcells.website.server.security.authentication.UserSession;

/**
 * An instance of this is itself responsible to only allow access to sensitive data to callers,
 * that have a {@link UserSession} with appropriate rights.
 * Currently, this is only a marker interface.
 * In other words, the interface hints, the special care should be taken regarding security.
 * 
 * @deprecated TODO There is no real need for that, because interfaces like {@link AccessControl} already do that.
 */
@Deprecated
public interface Firewall {
}
