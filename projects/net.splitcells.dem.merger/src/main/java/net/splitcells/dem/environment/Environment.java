/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

public interface Environment
		extends EnvironmentV, Closeable, Flushable {
/**
 * TODO Create a meta config in order to add arbitrary information, methods etc. to a config.
 * This also allows to omit reflection in order to close all {@link net.splitcells.dem.environment.resource.Resource}
 * during {@link #close()}, for instance.
 * This would also be a way to generate state reports or documentation via injection mechanisms.
 * Thereby, the whole environment configuration could be modelled as a table,
 * which would simplify the visualization.
 */

	/**
	 * It is not allowed to be called multiple times.
	 */
	void init();

	Configuration config();

}
