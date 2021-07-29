/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.dem.environment.config;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

/**
 * IDEA Get values out of properties file.
 */
public final class StaticFlags {
	public static final boolean ENFORCING_UNIT_CONSISTENCY = true;
	@Deprecated
	public static final boolean FUZZING = true;
	@Deprecated
	public static final boolean ENFORCING_INTEGRATION_CONSISTENCY = true;
	@Deprecated
	public static final boolean PROFILING_RUNTIME = true;
	@Deprecated
	public static final boolean PROFILING_MEMORY_USAGE = true;
	@Deprecated
	public static final boolean TELLING_STORY = true;
	@Deprecated
	public static final boolean WARNING = true;
	public static final boolean TRACING = true;

	private StaticFlags() {
		throw constructorIllegal();
	}
}