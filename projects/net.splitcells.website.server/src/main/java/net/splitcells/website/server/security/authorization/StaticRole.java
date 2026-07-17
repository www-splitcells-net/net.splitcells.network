/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.website.server.security.authorization;

/**
 * This interface is used in order to compactly create a new class of {@link Role},
 * that reserves its name in Java's package classpath.
 * Thereby, it is easier to ensure the uniqueness of {@link Role#name()}.
 */
public interface StaticRole extends Role {
    default String name() {
        return getClass().getName();
    }
}
