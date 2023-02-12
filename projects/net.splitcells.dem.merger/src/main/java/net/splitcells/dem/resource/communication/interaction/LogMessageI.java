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
package net.splitcells.dem.resource.communication.interaction;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.object.Discoverable;

public class LogMessageI<T> implements LogMessage<T> {

	public static <T> LogMessage<T> logMessage(T content, Discoverable context, LogLevel priority) {
		return new LogMessageI<T>(content, context, priority);
	}

	private final T content;
	private final LogLevel priority;
	private final Discoverable context;

	private LogMessageI(T content, Discoverable context, LogLevel priority) {
		this.content = content;
		this.context = context;
		this.priority = priority;

	}

	@Override
	public T content() {
		return content;
	}

	@Override
	public LogLevel priority() {
		return priority;
	}

	@Override
	public List<String> path() {
		return context.path();
	}

}
