/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource.communication;

import net.splitcells.dem.lang.annotations.JavaLegacy;

@JavaLegacy
public interface Flushable extends java.io.Flushable {

	/**
	 * Used in order to circumvent checked exceptions.
	 */
	void flush();

}
