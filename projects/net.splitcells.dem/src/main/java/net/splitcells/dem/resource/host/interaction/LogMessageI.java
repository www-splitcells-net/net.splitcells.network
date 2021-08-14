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
package net.splitcells.dem.resource.host.interaction;

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
