/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.execution;

/**
 * <p>The {@link ImplicitEffect} is the base interface in order to contain all side effects via an implicit API.
 * This works like {@link ExplicitEffect} with the difference,
 * that the implicit version has no general synchronization method.
 * The synchronization is done by this object without any hints for the API user.</p>
 */
public interface ImplicitEffect {
}
