/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

import net.splitcells.website.server.security.authentication.UserSession;

/**
 * <p>Implementations should be thread safe, in order to minimize the number of {@link Authorizer} used at once.
 * This also avoid race conditions,
 * if a none thread safe implementation is accidentally used in multithreaded environments by accident.</p>
 */
public interface Authorizer {
    boolean hasRole(UserSession userSession, Role role);
}
