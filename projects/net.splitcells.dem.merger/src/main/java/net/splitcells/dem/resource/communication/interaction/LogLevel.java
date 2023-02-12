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

import net.splitcells.dem.data.order.Ordered;
import net.splitcells.dem.data.order.Ordering;

import static net.splitcells.dem.environment.config.StaticFlags.ENFORCING_UNIT_CONSISTENCY;
import static net.splitcells.dem.data.order.Ordering.*;
import static org.assertj.core.api.Assertions.assertThat;

public enum LogLevel implements Ordered<LogLevel> {
	/**
	 * Error which cannot be classified. This is highest severity.
	 */
	UNKNOWN_ERROR(0),
	/**
	 * Environment might be damaged.
	 */
	CRITICAL(1),
	/**
	 * Running application is damaged.
	 */
	ERROR(2),
	/**
	 * Not everything is working or something was done but could not be completed (i.e. additional user interaction is required).
	 */
	WARNING(3),
	/**
	 * Information that is important for interactive usage.
	 */
	INFO(4),
	/**
	 * Show all changes of state.
	 */
	DEBUG(5),
	/**
	 * Show everything that is executed.
	 */
	TRACE(6);

	private final int priority;

	LogLevel(int priority) {
		this.priority = priority;
	}

	@Override
	public Ordering compare_to(LogLevel arg) {
		if (priority == arg.priority) {
			return EQUAL;
		} else if (priority > arg.priority) {
			return GREATER_THAN;
		} else {
			if (ENFORCING_UNIT_CONSISTENCY) {
				assertThat(arg.priority).isGreaterThan(priority);
			}
			return LESSER_THAN;
		}
	}
}