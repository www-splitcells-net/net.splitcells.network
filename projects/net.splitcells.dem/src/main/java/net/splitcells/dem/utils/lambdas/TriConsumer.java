/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.utils.lambdas;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
	void apply(A a, B b, C c);
}
