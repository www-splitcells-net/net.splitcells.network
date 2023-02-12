/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;

/**
 * TODO Create a meta config in order to add arbitrary information, methods etc. to a config.
 * This also allows to omit reflection in order to close all {@link net.splitcells.dem.environment.resource.Resource}
 * during {@link #close()}, for instance.
 * This would also be a way to generate state reports or documentation via injection mechanisms.
 * Thereby, the whole environment configuration could be modelled as a table,
 * which would simplify the visualization.
 */
public interface Environment extends EnvironmentV, Closeable, Flushable {

	/**
	 * It is not allowed to be called multiple times.
	 */
	void init();

	Configuration config();

}
